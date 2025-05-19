package kg.nurtelecom.processflow.model.request

import kg.nurtelecom.processflow.model.common.Content

data class FlowAnswer(
    val responseItemId: String,
    val additionalContents: List<Content>? = null,
)