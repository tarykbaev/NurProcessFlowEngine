package kg.nurtelecom.processflow.custom_view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kg.nurtelecom.processflow.ui.web_view.ProcessFlowPdfViewerActivity


open class AppWebView(context: Context, attributeSet: AttributeSet) :
    WebView(context, attributeSet) {

    var fileChooserListener: ((intent: Intent?, callback: ValueCallback<Array<Uri>>?) -> Boolean)? = null

    var loadListener: PageLoadListener? = null
    var isNeedLockOnHost: Boolean = false
        set(value) {
            if (value && baseHost.isNullOrEmpty()) throw NoSuchFieldException("The baseHost field cannot be null or empty")
            field = value
        }
    var baseHost: String? = null

    init {
        initWebView()
    }

    private fun initWebView() {
        CookieManager.getInstance().setAcceptCookie(true)
        webViewClient = getWebViewClientInstance()
        webChromeClient = getWebViewChromeClientInstance()
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            setGeolocationEnabled(true)
            setSupportZoom(true)
        }
    }

    fun evaluateJavascript(script: String) {
        evaluateJavascript(script, null)
    }

    fun writeToWebViewLocalStorage(key: String, value: String) {
        evaluateJavascript("window.localStorage.setItem('$key','$value');", null)
    }

    open fun setupAdditionalSettings(additional: WebSettings.() -> Unit) {
        settings.apply(additional)
    }

    private fun getWebViewChromeClientInstance(): WebChromeClient {
        return object : WebChromeClient() {

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                loadListener?.onLocationPermissionRequest(origin, callback)
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                loadListener?.onPermissionRequest(request)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                loadListener?.onProgressChanged(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                loadListener?.onReceivedTitle(title ?: "")
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                val intent = fileChooserParams?.createIntent()
                intent?.type = "image/*"
                return fileChooserListener
                    ?.invoke(intent, filePathCallback)
                    ?.takeIf { it }
                    ?: super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            }
        }
    }

    private fun getWebViewClientInstance(): WebViewClient {
        return object : WebViewClient() {

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadListener?.onPageStarted()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                loadListener?.onPageFinished()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return handledOverrideUrlLoading(view, request?.url?.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return handledOverrideUrlLoading(view, url)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                return loadListener?.shouldInterceptRequest(view, request)
                    ?: super.shouldInterceptRequest(view, request)
            }
        }
    }

    private fun handledOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return when {
            isNeedLoadBaseUrl(url) -> {
                view?.loadUrl(baseHost ?: ""); true
            }

            isIntent(url) -> handleIntentUrl(url!!)
            isApplicationUri(url) -> openUriWithApplication(view, url)
            isLinkOnPdf(url) ->
            {
                ProcessFlowPdfViewerActivity.start(context, url ?: ""); true
            }

            else -> false
        }
    }

    fun isLinkOnPdf(url: String?) =
        url?.endsWith(".pdf") == true || url?.endsWith(".pdf/") == true

    private fun isNeedLoadBaseUrl(url: String?) =
        url == null || isNeedLockOnHost && !url.startsWith(baseHost!!)

    private fun isIntent(url: String?) = url?.startsWith("intent://") == true

    private fun isApplicationUri(url: String?): Boolean {
        return !url.isNullOrEmpty()
                && (url.startsWith("tg:")
                || url.startsWith("tel:")
                || url.startsWith("https://m.me/")
                || url.startsWith("whatsapp:"))
    }

    private fun handleIntentUrl(url: String): Boolean {
        Intent.parseUri(url, Intent.URI_INTENT_SCHEME)?.let { intent ->
            val info =
                context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (info != null) {
                goBack(); context.startActivity(intent)
            } else intent.getStringExtra("browser_fallback_url")?.let { url -> loadUrl(url) }
            return true
        }
        return false
    }

    private fun openUriWithApplication(view: WebView?, url: String?): Boolean {
        return try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            view?.goBack()
            true
        } catch (e: Exception) { return openPlayStoreWithApp(url) }
    }

    private fun openPlayStoreWithApp(url: String?): Boolean {
        val appPackage = getAppPackage(url) ?: return false
        return try {
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackage")
            )
            context.startActivity(playStoreIntent)
            true
        } catch (ex: Exception) {
            false
        }
    }

    private fun getAppPackage(url: String?): String? {
        return when {
            url.isNullOrEmpty() -> null
            url.startsWith("tg:") -> "org.telegram.messenger"
            url.startsWith("https://m.me/") -> "com.facebook.orca"
            url.startsWith("whatsapp:") -> "com.whatsapp"
            else -> null
        }
    }

    fun clearWebView() {
        clearHistory()
        clearCache(true)
        loadUrl("about:blank")
        removeAllViews()
        destroyDrawingCache()
        destroy()
        pauseTimers()
    }

    fun tryGoBack() = if (canGoBack()) {
        goBack(); true
    } else false

    open class PageLoadListener {
        open fun onPageStarted() {}
        open fun onProgressChanged(progress: Int) {}
        open fun onPageFinished() {}
        open fun onReceivedTitle(title: String) {}
        open fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            return null
        }

        open fun onPermissionRequest(request: PermissionRequest?) {}
        open fun onLocationPermissionRequest(
            origin: String?,
            callback: GeolocationPermissions.Callback?) {}
    }
}