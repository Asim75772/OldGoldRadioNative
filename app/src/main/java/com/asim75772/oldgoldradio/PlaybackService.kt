package com.asim75772.oldgoldradio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder

class PlaybackService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentIndex = 0

    companion object {

        const val ACTION_PLAY =
            "com.asim75772.oldgoldradio.PLAY"

        const val ACTION_STOP =
            "com.asim75772.oldgoldradio.STOP"

        const val ACTION_TOGGLE =
            "com.asim75772.oldgoldradio.TOGGLE"

        const val ACTION_NEXT =
            "com.asim75772.oldgoldradio.NEXT"

        const val ACTION_PREVIOUS =
            "com.asim75772.oldgoldradio.PREVIOUS"

        const val ACTION_VOLUME =
            "com.asim75772.oldgoldradio.VOLUME"

        const val EXTRA_INDEX =
            "extra_index"

        const val EXTRA_VOLUME =
            "extra_volume"

        private const val CHANNEL_ID =
            "old_gold_radio"

        private const val NOTIFICATION_ID = 1001

        private val STREAMS = arrayOf(
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
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        startForeground(
            NOTIFICATION_ID,
            createNotification()
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_PLAY -> {

                val index = intent.getIntExtra(
                    EXTRA_INDEX,
                    0
                )

                if (index in STREAMS.indices) {
                    currentIndex = index
                }

                playRadio()
            }

            ACTION_TOGGLE -> {

                if (mediaPlayer?.isPlaying == true) {
                    pauseRadio()
                } else {
                    playRadio()
                }
            }

            ACTION_NEXT -> {

                currentIndex++

                if (currentIndex >= STREAMS.size) {
                    currentIndex = 0
                }

                playRadio()
            }

            ACTION_PREVIOUS -> {

                currentIndex--

                if (currentIndex < 0) {
                    currentIndex = STREAMS.size - 1
                }

                playRadio()
            }

            ACTION_VOLUME -> {

                val volume = intent.getFloatExtra(
                    EXTRA_VOLUME,
                    1.0f
                )

                setVolume(volume)
            }

            ACTION_STOP -> {

                stopRadio()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                } else {
                    @Suppress("DEPRECATION")
                    stopForeground(true)
                }

                stopSelf()
            }

            else -> {
                playRadio()
            }
        }

        return START_STICKY
    }

    private fun playRadio() {

        if (currentIndex !in STREAMS.indices) {
            currentIndex = 0
        }

        try {

            mediaPlayer?.let {

                try {
                    it.stop()
                } catch (_: Exception) {
                }

                it.release()
            }

            mediaPlayer = null

            val url = STREAMS[currentIndex]

            mediaPlayer = MediaPlayer().apply {

                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(
                            AudioAttributes.USAGE_MEDIA
                        )
                        .setContentType(
                            AudioAttributes.CONTENT_TYPE_MUSIC
                        )
                        .build()
                )

                setDataSource(url)

                setOnPreparedListener { player ->
                    player.start()
                }

                setOnErrorListener { _, _, _ ->
                    stopRadio()
                    true
                }

                setOnCompletionListener {
                    stopRadio()
                }

                prepareAsync()
            }

        } catch (e: Exception) {

            e.printStackTrace()
            stopRadio()
        }
    }

    private fun pauseRadio() {

        try {

            mediaPlayer?.let {

                if (it.isPlaying) {
                    it.pause()
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun stopRadio() {

        try {

            mediaPlayer?.let {

                try {
                    if (it.isPlaying) {
                        it.stop()
                    }
                } catch (_: Exception) {
                }

                it.reset()
                it.release()
            }

        } catch (e: Exception) {

            e.printStackTrace()

        } finally {

            mediaPlayer = null
        }
    }

    private fun setVolume(volume: Float) {

        val safeVolume = volume.coerceIn(
            0.0f,
            1.0f
        )

        try {

            mediaPlayer?.setVolume(
                safeVolume,
                safeVolume
            )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun createNotification(): Notification {

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            Notification.Builder(
                this,
                CHANNEL_ID
            )
                .setContentTitle(
                    "Old Gold Radio"
                )
                .setContentText(
                    "Old Gold Radio"
                )
                .setSmallIcon(
                    android.R.drawable.ic_media_play
                )
                .setOngoing(true)
                .build()

        } else {

            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle(
                    "Old Gold Radio"
                )
                .setContentText(
                    "Old Gold Radio"
                )
                .setSmallIcon(
                    android.R.drawable.ic_media_play
                )
                .setOngoing(true)
                .build()
        }
    }

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Old Gold Radio",
                NotificationManager.IMPORTANCE_LOW
            )

            channel.description =
                "Old Gold Radio playback"

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {

        stopRadio()

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }
}
