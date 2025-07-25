package kg.nurtelecom.processflow.ui.main

import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.base.BaseVM
import kg.nurtelecom.processflow.extension.defaultSubscribe
import kg.nurtelecom.processflow.model.ContentTypes
import kg.nurtelecom.processflow.model.Event
import kg.nurtelecom.processflow.model.ProcessFlowScreenData
import kg.nurtelecom.processflow.model.common.Content
import kg.nurtelecom.processflow.model.common.FlowStatus
import kg.nurtelecom.processflow.model.request.FlowAnswer
import kg.nurtelecom.processflow.model.request.FlowResponse
import kg.nurtelecom.processflow.network.parser.ProcessFlowResponseParser
import kg.nurtelecom.processflow.repository.ProcessFlowRepository
import kg.nurtelecom.processflow.util.PictureUtil
import kg.nurtelecom.nur_text_recognizer.RecognizedMrz
import kg.nurtelecom.processflow.model.request.ProcessVariable
import java.io.File

abstract class ProcessFlowVM<T: ProcessFlowRepository>(protected val _repository: T) : BaseVM() {

    protected var failGetStateCounts = 0

    val loaderState = MutableLiveData(false)

    protected var processFlowId: String? = null
    protected var processFlowStatus: FlowStatus? = null

    protected var isProcessUncancellable = false

    protected fun showLoading(){ loaderState.postValue(true) }
    protected fun hideLoading(){ loaderState.postValue(false) }

    protected val _flowResponseParser: ProcessFlowResponseParser by lazy {
        ProcessFlowResponseParser()
    }

    val processFlowScreenDataLive = MutableLiveData<ProcessFlowScreenData>()

    val processVariable = MutableLiveData<ProcessVariable?>()

    fun requireProcessFlowId(): String = processFlowId ?: throw Exception("Process flow id is null")

    fun updateProcessInfo(processFlow: FlowResponse): FlowResponse {
        processFlowId = processFlow.processId
        processFlowStatus = processFlow.flowStatus
        return processFlow
    }

    fun updateProcessFlowId(newId: String) {
        processFlowId = newId
    }

    fun getCurrentProcessFlowId() = processFlowId

    fun restoreActiveFlow(possibleProcessTypes: List<String>, newSubProcessType: String? = null, parentProcessId: String? = null) = disposed {
        _repository
            .findActiveProcess(possibleProcessTypes, parentProcessId)
            .doOnSubscribe { showLoading() }
            .doOnTerminate { hideLoading() }
            .map { updateProcessInfo(it) }
            .map {
                if (it.processType in possibleProcessTypes) it
                else throw Exception("Process flow not exist")
            }
            .flatMap { dispatchValuesToLiveData(it) }
            .subscribe(
                { Event.ProcessFlowIsExist(it != null, subProcessFlowType = newSubProcessType) },
                { triggerEvent(Event.ProcessFlowIsExist(false, subProcessFlowType = newSubProcessType)) }
            )
    }

    fun commit(responseId: String, additionalContents: List<Content>? = null) = disposed {
        _repository
            .commit(requireProcessFlowId(), FlowAnswer(responseId, additionalContents))
            .doOnSubscribe { showLoading() }
            .doOnTerminate { hideLoading() }
            .map { updateProcessInfo(it) }
            .flatMap { dispatchValuesToLiveData(it) }
            .defaultSubscribe(onError = ::handleError)
    }

    fun startProcessFlow(startFlowRequest: Map<String, Any>) = disposed {
        _repository
            .startProcessFlow(startFlowRequest)
            .doOnSubscribe { showLoading() }
            .doOnTerminate { hideLoading() }
            .map { updateProcessInfo(it) }
            .flatMap { dispatchValuesToLiveData(it) }
            .defaultSubscribe(onError = ::handleError)
    }

    fun getState(showLoader: Boolean = true) = disposed {
        _repository
            .getProcessFlowState(requireProcessFlowId())
            .doOnSubscribe { if (showLoader) showLoading() }
            .doOnTerminate { if (showLoader) hideLoading() }
            .map { updateProcessInfo(it) }
            .flatMap { dispatchValuesToLiveData(it) }
            .defaultSubscribe(
                onSuccess = { failGetStateCounts = 0 },
                onError = ::handleOnGetStateFailure
            )
    }


    fun cancelProcessFlow() {
        if (processFlowId == null || isProcessUncancellable) {
            triggerEvent(Event.FlowCancelledCloseActivity)
            return
        }
        _repository
            .cancelProcessFlow(requireProcessFlowId())
            .defaultSubscribe(
                onSuccess = { triggerEvent(Event.FlowCancelledCloseActivity) },
                onError = { triggerEvent(Event.FlowCancelledCloseActivity) }
            )
    }


    fun upload(
        responseId: String,
        file: File,
        type: String,
        recognizedMrz: RecognizedMrz? = null,
        onSuccess: () -> Unit,
        onFail: (warningMessage: String, finishOnFail: Boolean) -> Unit
    ) {
        disposed {
            compressIfTooLarge(file)
                .flatMap { _repository.uploadAttachment(requireProcessFlowId(), file) }
                .observeOn(Schedulers.io())
                .flatMap {
                    val additionalData = mutableListOf<Content>()
                    additionalData.add(Content(it, type))
                    if (type in listOf(ContentTypes.PASSPORT_BACK_PHOTO, ContentTypes.FOREIGN_PASSPORT_PHOTO)) additionalData.add(Content(recognizedMrz ?: getDefaultMrz(), ContentTypes.RECOGNIZED_PASSPORT_DATA))
                    _repository.commit(
                        requireProcessFlowId(),
                        FlowAnswer(
                            responseId,
                            additionalData
                        )
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    onSuccess.invoke()
                    it
                }
                .map { updateProcessInfo(it) }
                .flatMap { dispatchValuesToLiveData(it) }
                .doOnSubscribe { showLoading() }
                .doOnTerminate { hideLoading() }
                .defaultSubscribe(onError = {
                    handleError(it)
                })
        }
    }

    fun uploadFiles(
        responseId: String,
        compressedFiles: List<Pair<String, File>>,
        getContentType: (String) -> String,
    ) {
        disposed {
            _repository.uploadFileAttachments(requireProcessFlowId(), compressedFiles)
                .observeOn(Schedulers.io())
                .flatMap {
                    val additionalData = mutableListOf<Content>()
                    it.forEach {
                        additionalData.add(Content(content = it.id, additionalContentType = getContentType(it.type)))
                    }
                    _repository.commit(
                        requireProcessFlowId(),
                        FlowAnswer(
                            responseId,
                            additionalData
                        )
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map { updateProcessInfo(it) }
                .flatMap { dispatchValuesToLiveData(it) }
                .doOnSubscribe { showLoading() }
                .doOnTerminate { hideLoading() }
                .defaultSubscribe(onError = {
                    handleError(it)
                })
        }
    }

    private fun compressIfTooLarge(file: File): Single<File?> {
        return if ((file.length() / 1024) <= MAX_AVAILABLE_FILE_SIZE) Single.just(file)
        else compressFile(file, SECONDARY_COMPRESSION_QUALITY)
    }

    protected fun compressFile(file: File): Single<File?> {
        return Single.create<File?> {
            val compressedFile = PictureUtil.compressImage(file, COMPRESSION_QUALITY)
            if (compressedFile != null) {
                it.onSuccess(compressedFile)
            } else {
                it.onError(NullPointerException())
            }
        }
    }

    protected fun compressFile(file: File, quality: Int = COMPRESSION_QUALITY): Single<File?> {
        return Single.create<File?> {
            val compressedFile = PictureUtil.compressImage(file, quality)
            if (compressedFile != null) {
                it.onSuccess(compressedFile)
            } else {
                it.onError(NullPointerException())
            }
        }
    }

    protected open fun handleOnGetStateFailure(it: Throwable) {
        if (failGetStateCounts > 5) {
            failGetStateCounts = 0
            val message = (it)?.message
            val event = if (message.isNullOrBlank()) Event.NotificationResId(R.string.nur_process_flow_unexpected_error)
            else Event.Notification(message)
            triggerEvent(event)
        } else {
            failGetStateCounts++
            handleError(it)
        }
    }

    protected open fun handleError(ex: Throwable) {
        if (processFlowId != null) getState()
        else {
            val message = ex.message
            val event = if (message.isNullOrBlank()) Event.NotificationResId(R.string.nur_process_flow_unexpected_error)
            else Event.Notification(message)
            triggerEvent(event)
        }
    }

    protected open fun dispatchValuesToLiveData(response: FlowResponse): Single<FlowResponse> {
        return Single.fromCallable {

            val allowedAnswers = mutableListOf<Any>()

            _flowResponseParser.parseButtons(response.allowedAnswer)?.let { allowedAnswers.addAll(it) }
            _flowResponseParser.parseInputField(response.allowedAnswer)?.let { allowedAnswers.add(it) }
            _flowResponseParser.parseRetry(response.allowedAnswer)?.let { allowedAnswers.add(it) }
            _flowResponseParser.parseWebView(response.allowedAnswer)?.let { allowedAnswers.addAll(it) }
            _flowResponseParser.parseInputForms(response.allowedAnswer)?.let { allowedAnswers.addAll(it) }

            this.isProcessUncancellable = response.screenState?.isUncancellable ?: false

            val screenData = ProcessFlowScreenData(
                screenKey = response.screenKey,
                state = response.screenState,
                allowedAnswer = allowedAnswers,
                message = response.messages
            )
            processFlowScreenDataLive.postValue(screenData)
            processVariable.postValue(response.processVariable)
            response
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchOptions(formId: String, parentSelectedOptionId: String = "") {
        disposable.add(_repository.fetchOptions(formId, parentSelectedOptionId, processFlowId)
            .doOnSubscribe { showLoading() }
            .doOnTerminate { hideLoading() }
            .subscribe({
                triggerEvent(Event.AdditionalOptionsFetched(formId, it))
            }, {}))
    }

    fun isProcessTerminated() : Boolean {
        return processFlowStatus in listOf(FlowStatus.TERMINATED, FlowStatus.COMPLETED)
    }

    private fun getDefaultMrz(): RecognizedMrz {
        return RecognizedMrz(null, null,null,null,null,null,null,null,null,null,null,null)
    }

    companion object {
        const val COMPRESSION_QUALITY = 80
        const val SECONDARY_COMPRESSION_QUALITY = 50
        const val MAX_AVAILABLE_FILE_SIZE = 1024
    }
}