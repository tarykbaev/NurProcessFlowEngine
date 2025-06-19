package kg.nurtelecom.processflow.model

import kg.nurtelecom.processflow.model.common.Content
import kg.nurtelecom.processflow.model.component.FlowButton
import kg.nurtelecom.processflow.model.input_form.FormResponse
import kg.nurtelecom.nur_text_recognizer.RecognizedMrz
import java.io.File

open class ProcessFlowCommit {

    object Initial : ProcessFlowCommit()

    class OnButtonClick(val buttonsInfo: FlowButton, val additionalContent: List<Content>? = null): ProcessFlowCommit()
    class OnFlowPhotoCaptured(val responseId: String, val filePath: String, val fileType: String, val mrz: RecognizedMrz?): ProcessFlowCommit()
    class CommitContentFormResponseId(val responseId: String, val content: List<Content>?): ProcessFlowCommit()
    class CommitUploadMultipleFiles(val responseId: String, val files: List<Pair<String, File>>): ProcessFlowCommit()

    class FetchAdditionalOptionsForDropDown(val formId: String, val parentSelectedOptionId: String = ""): ProcessFlowCommit()

    class OnLinkClicked(val link: String): ProcessFlowCommit()

    class HandleEvent(val event: Event) : ProcessFlowCommit()
}