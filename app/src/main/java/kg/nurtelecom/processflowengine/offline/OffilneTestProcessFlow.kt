package kg.nurtelecom.processflowengine.offline

import android.content.Context
import kg.nurtelecom.processflow.model.Event
import kg.nurtelecom.processflow.model.ProcessFlowCommit
import kg.nurtelecom.processflow.model.common.Content
import kg.nurtelecom.processflow.model.component.FlowButton
import kg.nurtelecom.processflow.repository.ProcessFlowRepository
import kg.nurtelecom.processflow.ui.main.ProcessFlowActivity
import kg.nurtelecom.processflow.ui.main.ProcessFlowVM

class OfflineTestProcessFlow : ProcessFlowActivity<TestVM>()  {

    override fun setupViews() {
        super.setupViews()
    }

    override val vm: TestVM by lazy {
        TestVM(this)
    }
    override val processType: String
        get() = "TEST_TYPE"

    override fun getProcessFlowStartParams(): Map<String, String> {
        return mapOf("identificationNumber" to "123456678")
    }

    override fun resolveButtonClickCommit(button: FlowButton?, additionalContent: List<Content>?) {
        if (button?.buttonId == "EXIT_NAVIGATE_TO_WALLET_MAIN") finish()
        else super.resolveButtonClickCommit(button, additionalContent)
    }
}

object MyCommit : ProcessFlowCommit()

sealed class MyEvent : Event() {
    object MySubEvent : MyEvent()
}

class TestRepo(context: Context) : ProcessFlowRepository(ProcessFlowApiImpl) {

}


class TestVM(context: Context) : ProcessFlowVM<ProcessFlowRepository>(TestRepo(context)) {

}
