package kg.nurtelecom.processflow.repository

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kg.nurtelecom.processflow.model.input_form.Option
import kg.nurtelecom.processflow.model.request.FlowAnswer
import kg.nurtelecom.processflow.model.request.FlowCancelRequest
import kg.nurtelecom.processflow.model.request.FlowCommitRequest
import kg.nurtelecom.processflow.model.request.FlowResponse
import kg.nurtelecom.processflow.model.request.FlowUploadedAttachment
import kg.nurtelecom.processflow.network.ProcessFlowNetworkApi
import kg.nurtelecom.processflow.util.PictureUtil
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

abstract class ProcessFlowRepository (
    private val _api: ProcessFlowNetworkApi,
) {

    fun findActiveProcess(possibleProcessTypes: List<String>, parentProcessId: String? = null): Single<FlowResponse?> =
        _api
            .findActiveProcess(possibleProcessTypes.joinToString { it }, parentProcessId = parentProcessId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun startProcessFlow(request: Map<String, Any>): Single<FlowResponse> =
        _api
            .startFlow(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getProcessFlowState(processId: String): Single<FlowResponse> =
        _api
            .getState(processId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun commit(processId: String, answer: FlowAnswer): Single<FlowResponse> =
        _api
            .commit(FlowCommitRequest(processId, answer))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun cancelProcessFlow(processId: String): Single<Boolean> {
        return _api
            .cancelFlow(FlowCancelRequest(processId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun uploadAttachment(processId: String, file: File): Single<String> {
        val requestBody = file.asRequestBody(MultipartBody.FORM)
        val body = MultipartBody.Part.createFormData("file", file.absolutePath, requestBody)
        val applicationId = MultipartBody.Part.createFormData("process_id", processId)
        return _api.uploadAttachment(applicationId, body)
            .flatMap { deleteFile(file, it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun uploadFileAttachments(processId: String, attachments: List<Pair<String, File>>): Single<List<FlowUploadedAttachment>> {
        val partBodies = attachments.map { createMultiPartBody(it.first, it.second) }.toTypedArray()
        val processIdBody = MultipartBody.Part.createFormData("process_id", processId)

        return _api.uploadFileAttachments(*partBodies, processIdBody).flatMap { result ->
            deleteFiles(attachments.map { it.second }).map { result }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun createMultiPartBody(name: String, file: File): MultipartBody.Part {
        val requestBody = file.asRequestBody(MultipartBody.FORM)
        return MultipartBody.Part.createFormData(name, file.absolutePath, requestBody)
    }

    fun fetchOptions(formId: String, parentSelectedId: String = "", processId: String? = ""): Single<List<Option>> {
        return _api.fetchAdditionalOptions(formId, parentSelectedId, processId = processId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun deleteFile(file: File, resultString: String): Single<String> {
        return Single.create {
            PictureUtil.deleteFile(file)
            it.onSuccess(resultString)
        }
    }

    private fun deleteFiles(files: List<File>): Single<Unit> {
        return Single.create {
            files.forEach {
                PictureUtil.deleteFile(it)
            }
            it.onSuccess(Unit)
        }
    }
}