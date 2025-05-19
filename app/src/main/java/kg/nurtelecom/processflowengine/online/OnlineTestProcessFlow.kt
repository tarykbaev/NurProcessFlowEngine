package kg.nurtelecom.processflowengine.online

import android.widget.Toast
import kg.nurtelecom.processflow.model.common.Content
import kg.nurtelecom.processflow.model.component.FlowButton
import kg.nurtelecom.processflow.network.ProcessFlowNetworkApi
import kg.nurtelecom.processflow.repository.ProcessFlowRepository
import kg.nurtelecom.processflow.ui.main.ProcessFlowActivity
import kg.nurtelecom.processflow.ui.main.ProcessFlowVM
import kg.nurtelecom.processflowengine.common.TestProcessPrefs
import kg.nurtelecom.processflowengine.online.util.RetrofitCreator

class OnlineTestProcessFlow : ProcessFlowActivity<OnlineTestVM>()  {

    private val prefs: TestProcessPrefs by lazy { TestProcessPrefs(this) }
    override val vm: OnlineTestVM by lazy { OnlineTestVM(RetrofitCreator.create(prefs.token, (intent.getStringExtra(EXTRA_BASE_URL) ?: "")).create(ProcessFlowNetworkApi::class.java)) }
    override val processType: String get() = intent.getStringExtra(EXTRA_PROCESS_TYPE) ?: ""

    override val possibleProcessTypesToRestore: List<String> get() = intent.getStringExtra(EXTRA_POSSIBLE_PROCESS_FLOWS)?.split(" ") ?: listOf(processType)

    override fun setupViews() {
        super.setupViews()
    }

    override fun getProcessFlowStartParams(): Map<String, Any> {
        val superMap = super.getProcessFlowStartParams()
        return superMap.toMutableMap().apply {
            put("identificationNumber", "123456678")
        }
    }

    override fun resolveButtonClickCommit(button: FlowButton?, additionalContent: List<Content>?) {
        when (button?.buttonId) {
            "OPEN_AGREEMENT_DOCUMETS" -> Toast.makeText(this, "Open documents fragment", Toast.LENGTH_SHORT).show()
            "EXIT_NAVIGATE_TO_WALLET_MAIN" -> finish()
            else -> super.resolveButtonClickCommit(button, additionalContent)
        }
    }

    companion object {
        const val EXTRA_PROCESS_TYPE = "EXTRA_PROCESS_TYPE"
        const val EXTRA_BASE_URL = "EXTRA_BASE_URL"
        const val EXTRA_POSSIBLE_PROCESS_FLOWS = "EXTRA_POSSIBLE_PROCESS_FLOWS"
    }
}

class OnlineTestVM(api: ProcessFlowNetworkApi) : ProcessFlowVM<ProcessFlowRepository>(OnlineTestRepo(api))
class OnlineTestRepo(api: ProcessFlowNetworkApi) : ProcessFlowRepository(api)