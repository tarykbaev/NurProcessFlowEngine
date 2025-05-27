package kg.nurtelecom.processflowengine.online

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.design2.chili2.extensions.setOnSingleClickListener
import com.design2.chili2.view.navigation_components.ChiliToolbar
import kg.nurtelecom.processflowengine.online.OnlineTestProcessFlow.Companion.EXTRA_BASE_URL
import kg.nurtelecom.processflowengine.online.OnlineTestProcessFlow.Companion.EXTRA_PROCESS_TYPE
import kg.nurtelecom.processflowengine.common.TestProcessPrefs
import kg.nurtelecom.processflowengine.databinding.ActivityOnlineFlowConfiguratorBinding
import kg.nurtelecom.processflowengine.online.OnlineTestProcessFlow.Companion.EXTRA_POSSIBLE_PROCESS_FLOWS
import kg.nurtelecom.processflowengine.online.OnlineTestProcessFlow.Companion.PHONE_NUMBER

class OnlineFlowConfiguratorActivity : AppCompatActivity() {

    lateinit var vb: ActivityOnlineFlowConfiguratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityOnlineFlowConfiguratorBinding.inflate(layoutInflater)
        setContentView(vb.root)

        vb.toolbar.initToolbar(ChiliToolbar.Configuration(this, centeredTitle = true, isNavigateUpButtonEnabled = true))

        val prefs = TestProcessPrefs(this)

        vb.baseUrl.setText(prefs.base_url.takeIf { it.isNotBlank() } ?: "")
        vb.baseUrl.setupClearTextButton()

        vb.etToken.setText(prefs.token)
        vb.etToken.setupClearTextButton()

        vb.etProcessFlowId.setText(prefs.process_type.takeIf { it.isNotBlank() } ?: "")
        vb.etProcessFlowId.setupClearTextButton()

        vb.phoneNumber.setText(prefs.phoneNumber.takeIf { it.isNotBlank() } ?: "")
        vb.phoneNumber.setupClearTextButton()

        vb.btnStart.setOnSingleClickListener {
            val i = Intent(this, OnlineTestProcessFlow::class.java)

            vb.baseUrl.getInputText().let {
                prefs.base_url = it
                i.putExtra(EXTRA_BASE_URL, it)
            }

            vb.etToken.getInputText().let { prefs.token = it }

            vb.etProcessFlowId.getInputText().let {
                prefs.process_type = it
                i.putExtra(EXTRA_PROCESS_TYPE, it)
            }
            vb.phoneNumber.getInputText().takeIf { it.isNotBlank() }?.let {
                prefs.phoneNumber = it
                i.putExtra(PHONE_NUMBER, it)
            }

            startActivity(i)
        }
    }
}