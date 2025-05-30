package kg.nurtelecom.processflowengine

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.design2.chili2.view.navigation_components.ChiliToolbar
import kg.nurtelecom.processflowengine.databinding.ActivityMainBinding
import kg.nurtelecom.processflowengine.offline.OfflineFlowConfig
import kg.nurtelecom.processflowengine.online.OnlineFlowConfiguratorActivity
import kg.nurtelecom.processflowengine.personification.PersonificationFlowConfig

class MainActivity : AppCompatActivity() {

    lateinit var vb: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)
        vb.toolbar.initToolbar(ChiliToolbar.Configuration(this, centeredTitle = true, isNavigateUpButtonEnabled = false))

        vb.btnStartReal.setOnClickListener { startActivity(Intent(this, OnlineFlowConfiguratorActivity::class.java)) }
        vb.btnStart.setOnClickListener { startActivity(Intent(this, OfflineFlowConfig::class.java)) }
        vb.btnStartPersonifcation.setOnClickListener { startActivity(Intent(this, PersonificationFlowConfig::class.java)) }

        vb.swTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            setupDarkTheme(isChecked)
        }
    }

    private fun setupDarkTheme(isDark: Boolean) {
        when (isDark) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


}


