package com.asim75772.oldgoldradio

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {

            webView = WebView(this)

            setContentView(webView)

            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = true
                allowContentAccess = true
            }

            webView.webViewClient = WebViewClient()

            webView.webChromeClient = WebChromeClient()

            webView.loadUrl(
                "https://asim75772.github.io/80s90s-old-is-gold/"
            )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    override fun onDestroy() {

        try {
            if (::webView.isInitialized) {
                webView.stopLoading()
                webView.destroy()
            }
        } catch (_: Exception) {
        }

        super.onDestroy()
    }
}
