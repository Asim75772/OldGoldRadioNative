package com.asim75772.oldgoldradio

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private val streams = arrayOf(
        "https://stream.zeno.fm/n2fd0edh9k8uv",
        "https://stream.zeno.fm/0ghtfp8ztm0uv",
        "https://stream.zeno.fm/87xam8pf7tzuv",
        "https://stream.zeno.fm/t39watus1p8uv",
        "https://stream.zeno.fm/0zkr7x8ztm0uv",
        "https://stream.zeno.fm/u0hrd3xkzhhvv",
        "https://stream.zeno.fm/fdgs82xkzhhvv",
        "https://stream.zeno.fm/6n6ewddtad0uv",
        "https://stream.zeno.fm/60ef4p33vxquv"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        webView.addJavascriptInterface(
            RadioBridge(),
            "NativeRadio"
        )

        webView.loadUrl(
            "https://asim75772.github.io/80s90s-old-is-gold/"
        )
    }

    inner class RadioBridge {

        @JavascriptInterface
        fun playChannel(index: Int) {

            if (index !in streams.indices) return

            val intent = android.content.Intent(
                this@MainActivity,
                PlaybackService::class.java
            ).apply {

                action = PlaybackService.ACTION_PLAY

                putExtra(
                    PlaybackService.EXTRA_INDEX,
                    index
                )
            }

            ContextCompat.startForegroundService(
                this@MainActivity,
                intent
            )
        }

        @JavascriptInterface
        fun togglePlay() {

            val intent = android.content.Intent(
                this@MainActivity,
                PlaybackService::class.java
            ).apply {

                action = PlaybackService.ACTION_TOGGLE
            }

            ContextCompat.startForegroundService(
                this@MainActivity,
                intent
            )
        }

        @JavascriptInterface
        fun next() {

            val intent = android.content.Intent(
                this@MainActivity,
                PlaybackService::class.java
            ).apply {

                action = PlaybackService.ACTION_NEXT
            }

            ContextCompat.startForegroundService(
                this@MainActivity,
                intent
            )
        }

        @JavascriptInterface
        fun previous() {

            val intent = android.content.Intent(
                this@MainActivity,
                PlaybackService::class.java
            ).apply {

                action = PlaybackService.ACTION_PREVIOUS
            }

            ContextCompat.startForegroundService(
                this@MainActivity,
                intent
            )
        }

        @JavascriptInterface
        fun setVolume(value: Double) {

            val intent = android.content.Intent(
                this@MainActivity,
                PlaybackService::class.java
            ).apply {

                action = PlaybackService.ACTION_VOLUME

                putExtra(
                    PlaybackService.EXTRA_VOLUME,
                    value.toFloat()
                )
            }

            startService(intent)
        }
    }

    override fun onDestroy() {

        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.destroy()
        }

        super.onDestroy()
    }
}
