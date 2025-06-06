package kg.nurtelecom.processflow.network

import io.reactivex.Single
import kg.nurtelecom.processflow.model.input_form.Option
import kg.nurtelecom.processflow.model.request.FlowCancelRequest
import kg.nurtelecom.processflow.model.request.FlowCommitRequest
import kg.nurtelecom.processflow.model.request.FlowResponse
import kg.nurtelecom.processflow.model.request.FlowUploadedAttachment
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProcessFlowNetworkApi {

    @GET("v2/process/info/find-running-primary")
    fun findActiveProcess(
        @Query("flow_types") possibleProcessTypes: String,
        @Query("parent_instance_key") parentProcessId: String? = null,
    ): Single<FlowResponse?>

    @POST("v2/process/start")
    @JvmSuppressWildcards
    fun startFlow(@Body request: Map<String, Any>): Single<FlowResponse>

    @GET("v2/process/state")
    fun getState(@Query("process_id") processId: String? = null): Single<FlowResponse>

    @POST("v2/process/commit")
    fun commit(@Body request: FlowCommitRequest): Single<FlowResponse>

    @Multipart
    @POST("v2/attachments/upload")
    fun uploadAttachment(
        @Part process_id: MultipartBody.Part? = null,
        @Part file: MultipartBody.Part? = null,
    ): Single<String>

    @Multipart
    @POST("v2/native/attachments/upload")
    fun uploadFileAttachments(
        @Part vararg attachments: MultipartBody.Part?,
    ): Single<List<FlowUploadedAttachment>>

    @GET("v2/dictionaries/form-item/options/{form_item_id}/{parent_selected_option_id}")
    fun fetchAdditionalOptions(
        @Path("form_item_id") formId: String,
        @Path("parent_selected_option_id") parentSelectedOptionId: String,
        @Query("instanceKey") processId: String? = null,
    ): Single<List<Option>>

    @POST("v2/process/cancel")
    fun cancelFlow(@Body request: FlowCancelRequest): Single<Boolean>

}