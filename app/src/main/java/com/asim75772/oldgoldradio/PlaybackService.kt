package com.asim75772.oldgoldradio

import android.content.Intent
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService : MediaSessionService() {

    companion object {
        const val ACTION_PLAY = "com.asim75772.oldgoldradio.PLAY"
        const val ACTION_TOGGLE = "com.asim75772.oldgoldradio.TOGGLE"
        const val ACTION_NEXT = "com.asim75772.oldgoldradio.NEXT"
        const val ACTION_PREVIOUS = "com.asim75772.oldgoldradio.PREVIOUS"
        const val ACTION_VOLUME = "com.asim75772.oldgoldradio.VOLUME"
        const val EXTRA_INDEX = "index"
        const val EXTRA_VOLUME = "volume"

        private val streams = listOf(
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

        private val names = listOf(
            "Hindi Evergreen",
            "Kishore Kumar Radio",
            "Lata Mangeshkar Radio",
            "Goldy Mukesh",
            "Mohammed Rafi Radio",
            "Radio Retro Bollywood",
            "Retro Bollywood 90s",
            "Bollywood Gaane Purane",
            "Manna Dey"
        )
    }

    private lateinit var player: ExoPlayer
    private var session: MediaSession? = null
    private var current = 0

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build()
        player.setHandleAudioBecomingNoisy(true)

        session = MediaSession.Builder(this, player)
            .setId("OldGoldRadioSession")
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val index = intent.getIntExtra(EXTRA_INDEX, 0)
                playIndex(index)
            }
            ACTION_TOGGLE -> {
                if (player.isPlaying) player.pause() else player.play()
            }
            ACTION_NEXT -> playIndex((current + 1) % streams.size)
            ACTION_PREVIOUS -> playIndex((current - 1 + streams.size) % streams.size)
            ACTION_VOLUME -> {
                val v = intent.getFloatExtra(EXTRA_VOLUME, 0.8f)
                player.volume = v.coerceIn(0f, 1f)
            }
        }
        return START_STICKY
    }

    private fun playIndex(index: Int) {
        if (index !in streams.indices) return
        current = index

        val metadata = MediaMetadata.Builder()
            .setTitle(names[index])
            .setArtist("80s & 90s Old Is Gold")
            .setAlbum("Old Is Gold Radio")
            .setArtworkUri(Uri.parse("android.resource://$packageName/drawable/old_gold_logo"))
            .build()

        val item = MediaItem.Builder()
            .setMediaId(index.toString())
            .setUri(streams[index])
            .setMediaMetadata(metadata)
            .build()

        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return session
    }

    override fun onDestroy() {
        session?.release()
        player.release()
        session = null
        super.onDestroy()
    }
}
