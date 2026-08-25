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
        const val ACTION_PLAY = "com.asim75772.oldgoldradio.PLAY"
        const val ACTION_STOP = "com.asim75772.oldgoldradio.STOP"

        private const val CHANNEL_ID = "old_gold_radio"
        private const val NOTIFICATION_ID = 1001

        // এখানে আপনার radio stream URL বসাবেন
        const val RADIO_URL = "https://example.com/radio.mp3"
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

            ACTION_STOP -> {
                stopRadio()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
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
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )

                setDataSource(RADIO_URL)

                setOnPreparedListener {
                    start()
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

    private fun stopRadio() {

        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {
        }

        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun createNotification(): Notification {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Old Gold Radio")
                .setContentText("Radio is playing")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .build()

        } else {

            Notification.Builder(this)
                .setContentTitle("Old Gold Radio")
                .setContentText("Radio is playing")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .build()
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Old Gold Radio",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {

        stopRadio()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
