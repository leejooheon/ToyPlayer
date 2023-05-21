package com.jooheon.clean_architecture.features.wikipedia.detail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView

const val BASE_URL = "https://en.wikipedia.org/api/rest_v1/page/html/";

@Composable
fun WikipediaDatailScreen(
    keyword: String
) {
    val loadingState = remember { mutableStateOf(false) }

    MyWebView(loadingState, keyword)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun MyWebView(
    loadingState: MutableState<Boolean>,
    keyword: String
) {
    val backEnabled = remember { mutableStateOf(false) }
    var webView: WebView? = null
    val url = BASE_URL + keyword

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = MyWebViewClient(backEnabled, loadingState)

                settings.javaScriptEnabled = true
                loadUrl(url)

                webView = this
            }

        }, update = {
            webView = it
        }
    )

    BackHandler(enabled = backEnabled.value) {
        webView?.goBack()
    }
}


private fun MyWebViewClient(
    backEnabled: MutableState<Boolean>,
    loadingState: MutableState<Boolean>
) = object : WebViewClient() {
    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        backEnabled.value = view.canGoBack()
        loadingState.value = true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        loadingState.value = false
    }
}