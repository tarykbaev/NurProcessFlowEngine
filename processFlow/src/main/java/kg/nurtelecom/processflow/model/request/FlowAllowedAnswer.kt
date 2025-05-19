package kg.nurtelecom.processflow.model.request

import com.google.gson.JsonElement
import kg.nurtelecom.processflow.model.component.FlowResponseType

data class FlowAllowedAnswer(
    val responseType: FlowResponseType,
    val responseItem: JsonElement
)

