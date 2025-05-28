package kg.nurtelecom.processflowengine.personification

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.design2.chili2.view.navigation_components.ChiliToolbar
import kg.nurtelecom.processflowengine.databinding.ActivityOfflineFlowConfiguratorBinding

class PersonificationFlowConfig : AppCompatActivity() {

    private lateinit var vb: ActivityOfflineFlowConfiguratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityOfflineFlowConfiguratorBinding.inflate(layoutInflater)
        setContentView(vb.root)

        vb.toolbar.initToolbar(ChiliToolbar.Configuration(this, centeredTitle = true, isNavigateUpButtonEnabled = true))

        vb.startCode.setText(PersonificationFlowApiImpl.FIRST_STEP_KEY)
        vb.delay.setText((PersonificationFlowApiImpl.REQUESTS_DELAY).toString())
        vb.btnStart.setOnClickListener {
            PersonificationFlowApiImpl.REQUESTS_DELAY = vb.delay.getInputText().toLongOrNull() ?: 300L
            openFlow(vb.startCode.getInputText().takeIf { it.isNotBlank() } ?: PersonificationFlowApiImpl.FIRST_STEP_KEY)
        }
        vb.btnStartForm.setOnClickListener { openFlow("OTP_INPUT") }
        vb.btnStartAgreemrnt.setOnClickListener { openFlow("passport_form") }
        vb.btnStartOferta.setOnClickListener { openFlow("WEB_VIEW_OFERTA") }
        vb.btnStartCallWebView.setOnClickListener { openFlow("VIDEO_IDENT_BUTTON") }
        vb.btnStartOtp.setOnClickListener { openFlow("OTP") }
        vb.btnStartTimer.setOnClickListener { openFlow("ANY_WEB_VIEW_ID") }

    }


    private fun openFlow(startPoint: String) {
        val i = Intent(this, PersonificationTestProcessFlow::class.java)
        PersonificationFlowApiImpl.FIRST_STEP_KEY = startPoint
        startActivity(i)
    }

}