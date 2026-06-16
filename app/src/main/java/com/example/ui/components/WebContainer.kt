package com.example.ui.components

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.webkit.WebAdInterface

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebContainer(
    url: String,
    onPageStarted: (String?) -> Unit,
    onPageFinished: (String?) -> Unit,
    onLoadError: () -> Unit,
    onWebViewCreated: (WebView) -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = androidx.compose.ui.platform.LocalContext.current as? Activity

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                // Layout configurations
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    allowFileAccess = true
                    allowContentAccess = true
                }

                // Add the AdMob bridge interface
                activity?.let {
                    addJavascriptInterface(WebAdInterface(it), "AndroidAdInterface")
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        onPageStarted(url)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onPageFinished(url)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        // Ignore subresources, only focus on main frame connection errors
                        if (request?.isForMainFrame == true) {
                            onLoadError()
                        }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    // Handle chrome tasks like javascript alerts if required
                }

                onWebViewCreated(this)
            }
        },
        update = { webView ->
            // Let the activity/parent manage content loading if url changes or refresh of pages
        }
    )
}
