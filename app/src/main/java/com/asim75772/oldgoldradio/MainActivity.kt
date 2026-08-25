package com.asim75772.oldgoldradio

import android.annotation.SuppressLint
import android.content.Intent
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
        }

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(
                view: WebView,
                url: String
            ) {
                super.onPageFinished(view, url)
                injectNativeRadioBridge()
            }
        }

        webView.webChromeClient = WebChromeClient()

        webView.addJavascriptInterface(
            RadioBridge(),
            "NativeRadio"
        )

        webView.loadUrl(
            "https://asim75772.github.io/80s90s-old-is-gold/"
        )
    }

    private fun injectNativeRadioBridge() {

        val js = """
            (function() {

                if (window.__nativeRadioInstalled) {
                    return;
                }

                window.__nativeRadioInstalled = true;

                window.playChannel = function(index) {
                    try {

                        var i = Number(index);

                        var c = window.channels &&
                                window.channels[i];

                        if (c) {

                            var t =
                                document.getElementById(
                                    'nowTitle'
                                );

                            var d =
                                document.getElementById(
                                    'nowDesc'
                                );

                            if (t) {
                                t.textContent = c.name;
                            }

                            if (d) {
                                d.textContent =
                                    c.description || '';
                            }
                        }

                        NativeRadio.playChannel(i);

                        var b =
                            document.getElementById(
                                'playButton'
                            );

                        if (b) {
                            b.textContent = '❚❚';
                        }

                    } catch (e) {
                        console.log(e);
                    }
                };

                window.togglePlay = function() {
                    try {
                        NativeRadio.togglePlay();
                    } catch (e) {
                        console.log(e);
                    }
                };

                window.next = function() {
                    try {
                        NativeRadio.next();
                    } catch (e) {
                        console.log(e);
                    }
                };

                window.previous = function() {
                    try {
                        NativeRadio.previous();
                    } catch (e) {
                        console.log(e);
                    }
                };

                window.setVolume = function(v) {
                    try {
                        NativeRadio.setVolume(Number(v));
                    } catch (e) {
                        console.log(e);
                    }
                };

            })();
        """.trimIndent()

        webView.evaluateJavascript(js, null)
    }

    inner class RadioBridge {

        @JavascriptInterface
        fun playChannel(index: Int) {

            if (index !in streams.indices) return

            val intent =
                Intent(
                    this@MainActivity,
                    PlaybackService::class.java
                ).apply {

                    action =
                        PlaybackService.ACTION_PLAY

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

            val intent =
                Intent(
                    this@MainActivity,
                    PlaybackService::class.java
                ).apply {

                    action =
                        PlaybackService.ACTION_TOGGLE
                }

            ContextCompat.startForegroundService(
                this@MainActivity,
                intent
            )
        }

        @JavascriptInterface
        fun next() {

            val intent =
                Intent(
                    this@MainActivity,
                    PlaybackService::class.java
                ).apply {

                    action =
                        PlaybackService.ACTION_NEXT
                }

            ContextCompat.startForegroundService(
                this@MainActivity,
                intent
            )
        }

        @JavascriptInterface
        fun previous() {

            val intent =
                Intent(
                    this@MainActivity,
                    PlaybackService::class.java
                ).apply {

                    action =
                        PlaybackService.ACTION_PREVIOUS
                }

            ContextCompat.startForegroundService(
                this@MainActivity,
                intent
            )
        }

        @JavascriptInterface
        fun setVolume(value: Double) {

            val intent =
                Intent(
                    this@MainActivity,
                    PlaybackService::class.java
                ).apply {

                    action =
                        PlaybackService.ACTION_VOLUME

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
