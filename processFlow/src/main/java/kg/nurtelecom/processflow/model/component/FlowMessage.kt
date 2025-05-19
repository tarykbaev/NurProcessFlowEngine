package kg.nurtelecom.processflow.model.component

data class FlowMessage (
    val id: String,
    val content: String = "",
    val contentType: kg.nurtelecom.processflow.model.component.FlowMessageContentType,
    val messageType: kg.nurtelecom.processflow.model.component.FlowMessageType,
)

enum class FlowMessageContentType {
    TEXT, TEXT_HTML, IMAGE_URL
}

enum class FlowMessageType {
    USER, SYSTEM
}

