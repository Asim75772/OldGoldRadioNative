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

        private const val NOTIFICATION_ID =
            1001

        /*
         * আপাতত এই URL রাখা হলো।
         * পরে আপনার আসল radio stream URL এখানে বসাব।
         */
        const val RADIO_URL =
            "https://example.com/radio.mp3"
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
                playRadio()
            }

            ACTION_TOGGLE -> {
                if (mediaPlayer?.isPlaying == true) {
                    pauseRadio()
                } else {
                    playRadio()
                }
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

            ACTION_NEXT -> {
                playRadio()
            }

            ACTION_PREVIOUS -> {
                playRadio()
            }

            ACTION_VOLUME -> {
                val volume =
                    intent.getFloatExtra(EXTRA_VOLUME, 1.0f)

                setVolume(volume)
            }

            else -> {
                playRadio()
            }
        }

        return START_STICKY
    }

    private fun playRadio() {

        if (mediaPlayer?.isPlaying == true) {
            return
        }

        try {

            mediaPlayer?.release()

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

                setDataSource(RADIO_URL)

                setOnPreparedListener { player ->
                    player.start()
                }

                setOnCompletionListener {
                    stopRadio()
                }

                setOnErrorListener { _, _, _ ->
                    stopRadio()
                    true
                }

                prepareAsync()
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun pauseRadio() {

        try {

            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun stopRadio() {

        try {

            if (mediaPlayer != null) {

                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                }

                mediaPlayer?.reset()
                mediaPlayer?.release()
            }

        } catch (e: Exception) {

            e.printStackTrace()

        } finally {

            mediaPlayer = null
        }
    }

    private fun setVolume(volume: Float) {

        val safeVolume =
            volume.coerceIn(0.0f, 1.0f)

        mediaPlayer?.setVolume(
            safeVolume,
            safeVolume
        )
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
                    "Old Gold Radio is playing"
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
                    "Old Gold Radio is playing"
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

            val channel =
                NotificationChannel(
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

            manager.createNotificationChannel(
                channel
            )
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
