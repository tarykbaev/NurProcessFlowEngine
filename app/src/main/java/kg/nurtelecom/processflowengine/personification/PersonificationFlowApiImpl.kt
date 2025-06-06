package kg.nurtelecom.processflowengine.personification

import android.util.Log
import com.google.gson.Gson
import io.reactivex.Single
import kg.nurtelecom.processflow.model.input_form.Option
import kg.nurtelecom.processflow.model.request.FlowCancelRequest
import kg.nurtelecom.processflow.model.request.FlowCommitRequest
import kg.nurtelecom.processflow.model.request.FlowResponse
import kg.nurtelecom.processflow.model.request.FlowUploadedAttachment
import kg.nurtelecom.processflow.network.ProcessFlowNetworkApi
import okhttp3.MultipartBody
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object PersonificationFlowApiImpl : ProcessFlowNetworkApi {

    var FIRST_STEP_KEY = "start"
    var REQUESTS_DELAY = 300L

    override fun startFlow(request: Map<String, Any>): Single<FlowResponse> {
        Log.d("SAMPLE_TESTER", "startFlow: request: $request")
        return Single.just(
            Gson().fromJson(PersonificationMocker.mock.get(FIRST_STEP_KEY), FlowResponse::class.java)
        ).delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

    override fun findActiveProcess(
        @Query(value = "flow_types") possibleProcessTypes: String,
        @Query(value = "parent_instance_key") parentProcessId: String?
    ): Single<FlowResponse?> {
        Log.d("SAMPLE_TESTER", "getFlowStatus: Response null")
        return Single.error(java.lang.NullPointerException())
    }

    override fun getState(processId: String?): Single<FlowResponse> {
        Log.d("SAMPLE_TESTER", "getState")
        return Single.just(
            Gson().fromJson(PersonificationMocker.mock.get("FLOW_STATE"), FlowResponse::class.java)
        ).delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

    override fun commit(request: FlowCommitRequest): Single<FlowResponse> {
        Log.d("SAMPLE_TESTER", "commit: $request")
        return Single.just(
            Gson().fromJson(PersonificationMocker.mock.get(request.answer.responseItemId), FlowResponse::class.java)
        ).delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

    override fun uploadAttachment(
        process_id: okhttp3.MultipartBody.Part?,
        file: okhttp3.MultipartBody.Part?
    ): io.reactivex.Single<String> {
        Log.d("SAMPLE_TESTER", "upload")
        return Single.just("attachment").delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

    override fun uploadFileAttachments(vararg attachments: MultipartBody.Part?): Single<List<FlowUploadedAttachment>> {
        Log.d("SAMPLE_TESTER", "upload attachments")
        return Single.just(listOf(FlowUploadedAttachment("attachment_id", "attachment_type")))
    }

    override fun fetchAdditionalOptions(
        formId: String,
        parentSelectedOptionId: String,
        processId: String?,
    ): io.reactivex.Single<List<Option>> {
        Log.d("SAMPLE_TESTER", "fetchAdditionalOptions")
        return Single.just(listOf(
            Option("1", label = "Бишкек"),
            Option("2", label = "Токмок"),
            Option("3", label = "Ош"),
            Option("4", label = "Талас"),
            Option("5", label = "Нарын"),
        )).delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

    override fun cancelFlow(request: FlowCancelRequest): io.reactivex.Single<Boolean> {
        Log.d("SAMPLE_TESTER", "cancelFlow")
        return Single.just(true).delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

}