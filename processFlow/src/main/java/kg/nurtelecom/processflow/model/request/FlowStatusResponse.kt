package kg.nurtelecom.processflow.model.request

import com.google.gson.annotations.SerializedName
import kg.nurtelecom.processflow.model.common.FlowStatus

data class FlowStatusResponse(
    @SerializedName("process_id")
    val processId: String? = null,
    @SerializedName("process_status")
    val flowStatus: FlowStatus? = null,
)

