package com.kostaslou.gifsoundit.opengs.managers

import android.content.Context
import androidx.annotation.UiThread
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import dagger.hilt.android.qualifiers.ActivityContext
import timber.log.Timber
import javax.inject.Inject

class ExoplayerManager @Inject constructor(
    @ActivityContext private val context: Context
) {
    private val player = SimpleExoPlayer.Builder(context).build()
    private var listener: Listener? = null

    init {
        player.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> listener?.onPrepared()
                    else -> {
                    }
                }
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                Timber.e("Exoplayer error: ${error.localizedMessage}")
                listener?.onError(error.localizedMessage ?: "")
            }
        })
    }

    @UiThread
    fun setListener(listener: Listener) {
        this.listener = listener
    }

    @UiThread
    fun prepare(videoUrl: String, config: PlayerPrepareConfig) {
        if (player.isPlaying) {
            player.stop()
        }
        player.setMediaItem(MediaItem.fromUri(videoUrl))

        player.repeatMode = if (config.infiniteLoop) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
        player.volume = config.volume.toFloat()
        player.prepare()
    }

    @UiThread
    fun start() {
        player.play()
    }

    @UiThread
    fun stopAndRelease() {
        this.listener = null
        player.stop()
        player.release()
    }

    @UiThread
    fun bindPlayerView(playerView: PlayerView) {
        playerView.player = player
    }

    @UiThread
    fun unbindPlayerView(playerView: PlayerView) {
        playerView.player = null
    }

    data class PlayerPrepareConfig(
        val infiniteLoop: Boolean,
        val volume: Int
    )

    interface Listener {
        fun onPrepared()
        fun onError(message: String)
    }
}
