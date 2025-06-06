package kg.nurtelecom.processflow.model.component

import java.io.Serializable

data class FlowWebView(
    val id: String,
    val url: String? = null,
    val properties: WebViewProperties? = null,
): Serializable

data class WebViewProperties(
    val fileType: WebViewFileTypes? = null,
    val isShareEnabled: Boolean? = null,
    val faqUrl: String? = null,
): Serializable

enum class WebViewFileTypes {
    PDF, BASE_64
}