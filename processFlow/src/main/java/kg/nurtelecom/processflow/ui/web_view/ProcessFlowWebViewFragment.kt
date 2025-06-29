package kg.nurtelecom.processflow.ui.web_view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.base.BaseProcessScreenFragment
import kg.nurtelecom.processflow.base.process.BackPressHandleState
import kg.nurtelecom.processflow.custom_view.AppWebView
import kg.nurtelecom.processflow.databinding.NurProcessFlowFragmentProcessFlowWebViewBinding
import kg.nurtelecom.processflow.extension.getProcessFlowHolder
import kg.nurtelecom.processflow.model.ContentTypes
import kg.nurtelecom.processflow.model.ProcessFlowCommit
import kg.nurtelecom.processflow.model.ProcessFlowScreenData
import kg.nurtelecom.processflow.model.common.Content
import kg.nurtelecom.processflow.model.component.FlowWebView
import kg.nurtelecom.processflow.model.component.WebViewProperties

open class ProcessFlowWebViewFragment :
    BaseProcessScreenFragment<NurProcessFlowFragmentProcessFlowWebViewBinding>(), JsBridgeInterface {

    private var webViewId: String = ""
    override val buttonsLinearLayout: LinearLayout? get() = vb.llButtons
    override val unclickableMask: View? get() = null
    private var mGeoLocationRequestOrigin: String? = null
    private var mGeoLocationCallback: GeolocationPermissions.Callback? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            mGeoLocationCallback?.invoke(mGeoLocationRequestOrigin, it, false)
        }

    private var fileChooserWebViewCallback: ValueCallback<Array<Uri>>? = null
    private val fileChooserContract = object : ActivityResultContract<Intent, Array<Uri>?>() {
        override fun createIntent(context: Context, intent: Intent) = intent

        override fun parseResult(resultCode: Int, input: Intent?): Array<Uri>? {
            val result = WebChromeClient.FileChooserParams.parseResult(resultCode, input)
            return if (result == null) {
                val uri = mutableListOf<Uri>()
                val clipItemCount = input?.clipData?.itemCount ?: 0
                for (i in 0 until clipItemCount) {
                    input?.clipData?.getItemAt(i)?.uri?.let { uri.add(it) }
                }
                uri.toTypedArray()
            }
            else result
        }
    }
    private var fileChooserLauncher: ActivityResultLauncher<Intent>? = null

    override fun inflateViewBinging() =
        NurProcessFlowFragmentProcessFlowWebViewBinding.inflate(layoutInflater)

    override fun setScreenData(data: ProcessFlowScreenData?) {
        super.setScreenData(data)
        data?.allowedAnswer?.filterIsInstance<FlowWebView>()?.first()?.let {
            it.url?.let { getWebView().loadUrl(it) }
            webViewId = it.id
            handleProperties(it.properties)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileChooserLauncher = registerForActivityResult(fileChooserContract) {
            fileChooserWebViewCallback?.onReceiveValue(it)
        }
    }

    override fun onResume() {
        super.onResume()
        updateBackIcon()
    }

    override fun setupViews() = with(vb) {
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            onSwipeRefresh()
        }
        swipeRefreshLayout.isEnabled = false
        setupWebView()
    }

    open fun onSwipeRefresh() {
        vb.webView.reload()
    }

    private fun setupWebView() {
        getWebView().apply {
            setupAdditionalSettings {
                allowFileAccess = true
            }

            loadListener = object : AppWebView.PageLoadListener() {
                override fun onReceivedTitle(title: String) {
                    updateBackIcon()
                    updateToolbarTitleFromPage(title)
                }

                override fun onPageStarted() {
                    updateBackIcon()
                }

                override fun onProgressChanged(progress: Int) {
                    vb.progressBar.run {
                        setProgress(progress)
                        isVisible = progress < 100
                    }
                }

                override fun onLocationPermissionRequest(
                    origin: String?, callback: GeolocationPermissions.Callback?
                ) {
                        mGeoLocationCallback = callback
                        mGeoLocationRequestOrigin = origin
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
            addJavascriptInterface(this@ProcessFlowWebViewFragment, webViewJsInterfaceName)
            fileChooserListener = ::onFileChooseRequest
        }
    }

    open fun updateBackIcon() {
        val iconRes =
            if (getWebView().canGoBack()) com.design2.chili2.R.drawable.chili_ic_back
            else com.design2.chili2.R.drawable.chili_ic_close
        getProcessFlowHolder().setToolbarNavIcon(iconRes)
    }

    open fun updateToolbarTitleFromPage(title: String) {}

    open fun getWebView(): AppWebView = vb.webView

    open fun handleProperties(webViewProperties: WebViewProperties?) {
        webViewProperties?.faqUrl?.let {
            getProcessFlowHolder().setupToolbarEndIcon(R.drawable.nur_process_flow_ic_faq_24dp) {
                getProcessFlowHolder().commit(ProcessFlowCommit.OnLinkClicked(it))
            }
        }
    }

    private fun onFileChooseRequest(intent: Intent?, callback: ValueCallback<Array<Uri>>?): Boolean {
        if (intent == null || fileChooserLauncher == null) return false
        fileChooserWebViewCallback = callback
        fileChooserLauncher?.launch(intent)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        getWebView()?.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            getWebView().restoreState(savedInstanceState)
        }
    }

    override fun onDestroy() {
        getWebView().clearWebView()
        super.onDestroy()
    }

    @JavascriptInterface
    override fun setStringResultAndClose(result: String) {
        getProcessFlowHolder().commit(
            ProcessFlowCommit.CommitContentFormResponseId(
                webViewId, listOf(Content(result, ContentTypes.WEB_VIEW_RESULT))
            )
        )
    }

    @JavascriptInterface
    override fun isThemeLight(): String = isAppThemeLight

    @JavascriptInterface
    override fun getLocale(): String = appLocale

    override fun handleBackPress(): BackPressHandleState {
        return getWebView().run {
            if (canGoBack()) {
                getWebView().goBack()
                BackPressHandleState.HANDLED
            } else BackPressHandleState.NOT_HANDLE
        }
    }


    companion object {
        const val webViewJsInterfaceName = "MoyOAndroid"
        const val MANUAL_CLOSE_WEB_VIEW_STATUS = "MANUAL_CLOSE_WEB_VIEW_STATUS"
    }

}