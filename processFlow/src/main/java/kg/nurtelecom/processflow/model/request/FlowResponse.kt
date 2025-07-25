package kg.nurtelecom.processflow.model.request

import com.google.gson.annotations.SerializedName
import kg.nurtelecom.processflow.model.component.FlowMessage
import kg.nurtelecom.processflow.model.common.FlowStatus
import kg.nurtelecom.processflow.model.common.ScreenState
import java.io.Serializable

data class FlowResponse(
    @SerializedName("process_id")
    val processId: String? = null,
    @SerializedName("process_status")
    val flowStatus: FlowStatus? = null,
    @SerializedName("screen_code")
    val screenKey: String? = null,
    @SerializedName("screen_state")
    val screenState: ScreenState? = null,
    @SerializedName("messages")
    val messages: List<FlowMessage>? = null,
    @SerializedName("allowed_answers")
    val allowedAnswer: List<FlowAllowedAnswer>? = null,
    @SerializedName("definition_key")
    val processType: String? = null,
    @SerializedName("processExposedVariables")
    val processVariable: ProcessVariable? = null
): Serializable