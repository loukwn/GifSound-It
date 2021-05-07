package com.kostaslou.gifsoundit.opengs.view

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.postponeEnterTransition
import androidx.core.app.ActivityCompat.startPostponedEnterTransition
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gifsoundit.opengs.databinding.FragmentOpengsBinding
import com.kostaslou.gifsoundit.common.util.activityContext
import com.kostaslou.gifsoundit.opengs.GifState
import com.kostaslou.gifsoundit.opengs.OpenGSContract
import com.kostaslou.gifsoundit.opengs.SoundState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

internal class OpenGSViewImpl(
    private val context: Context,
    inflater: LayoutInflater,
    container: ViewGroup?,
    transitionName: String?,
) : OpenGSContract.View {

    private var listener: OpenGSContract.Listener? = null
    private val binding = FragmentOpengsBinding.inflate(inflater, container, false)

    private var soundYouTubePlayer: YouTubePlayer? = null

    init {
        // This is for the marquee setting to work
        if (transitionName != null) {
            binding.root.transitionName = transitionName
        }
        binding.statusLabel.isSelected = true

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            listener?.onBackButtonPressed()
        }

        binding.refreshButton.setOnClickListener {
            listener?.onRefreshButtonPressed()
        }

        binding.shareButton.setOnClickListener {
            listener?.onShareButtonPressed()
        }

        binding.playGIFLabel.setOnClickListener {
            listener?.onPlayGifLabelPressed()
        }

        binding.gifLabelCloseButton.setOnClickListener {
            binding.playGIFLayout.visibility = View.GONE
        }

        binding.addButton.setOnClickListener {
            listener?.onOffsetIncreaseButtonPressed()
        }

        binding.decreaseButton.setOnClickListener {
            listener?.onOffsetDecreaseButtonPressed()
        }

        binding.revertButton.setOnClickListener {
            listener?.onOffsetResetButtonPressed()
        }
    }

    override fun getSoundYoutubePlayerView(): YouTubePlayerView = binding.soundView

    override fun prepareGifImage(gifImageUrl: String) {
        binding.mp4View.isVisible = false
        binding.gifView.setOnClickListener {
            (binding.gifView.drawable as? GifDrawable)?.start()
        }

        Glide.with(context)
            .asGif()
            .load(gifImageUrl)
            .listener(object : RequestListener<GifDrawable> {
                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    listener?.onGifStateChanged(GifState.GIF_OK)
                    return false
                }

                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: Target<GifDrawable>?,
                    p3: Boolean
                ): Boolean {
                    listener?.onGifStateChanged(GifState.GIF_ERROR)
                    return false
                }
            })
            .into(binding.gifView)
    }

    override fun prepareGifVideo(gifVideoUrl: String) {
        binding.mp4View.isVisible = true
        binding.gifView.isVisible = false
        binding.mp4View.setOnErrorListener { _, _, _ ->
            // val statusText = when (extra) {
            //     MediaPlayer.MEDIA_ERROR_MALFORMED -> getString(R.string.opengs_error_gif_malformed)
            //     MediaPlayer.MEDIA_ERROR_IO -> getString(R.string.opengs_error_gif_io_error)
            //     MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> getString(R.string.opengs_error_gif_unsupported)
            //     MediaPlayer.MEDIA_ERROR_TIMED_OUT -> getString(R.string.opengs_error_gif_timed_out)
            //     else -> getString(
            //         R.string.opengs_error_gif_unknown_with_code,
            //         extra.toString()
            //     )
            // }

            listener?.onGifStateChanged(GifState.GIF_ERROR)

            true
        }

        binding.mp4View.setVideoPath(gifVideoUrl)
        binding.mp4View.setOnPreparedListener { mp ->
            listener?.onGifStateChanged(GifState.GIF_OK)

            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            mp.setVolume(0f, 0f)
        }
        binding.mp4View.setOnCompletionListener {
            binding.mp4View.seekTo(0)
            binding.mp4View.start()
        }
    }

    override fun prepareSoundYoutubeView(soundUrl: String, startSeconds: Float) {
        binding.soundView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                val playbackListener = object : AbstractYouTubePlayerListener() {
                    override fun onError(
                        youTubePlayer: YouTubePlayer,
                        error: PlayerConstants.PlayerError
                    ) { listener?.onSoundStateChanged(SoundState.SOUND_ERROR) }

                    override fun onStateChange(
                        youTubePlayer: YouTubePlayer,
                        state: PlayerConstants.PlayerState
                    ) {
                        when (state) {
                            PlayerConstants.PlayerState.VIDEO_CUED -> {
                                listener?.onSoundStateChanged(SoundState.SOUND_OK)
                            }
                            PlayerConstants.PlayerState.PLAYING -> {
                                listener?.onSoundStateChanged(SoundState.SOUND_STARTED)
                            }
                            else -> {
                            }
                        }
                    }
                }

                soundYouTubePlayer = youTubePlayer
                soundYouTubePlayer?.addListener(playbackListener)
                soundYouTubePlayer?.cueVideo(soundUrl, startSeconds)
            }

            override fun onError(
                youTubePlayer: YouTubePlayer,
                error: PlayerConstants.PlayerError
            ) { listener?.onSoundStateChanged(SoundState.SOUND_ERROR) }
        })
    }

    override fun startSound() {
        soundYouTubePlayer?.play()
    }

    override fun startGifImageFromTheStart() {
        binding.gifView.visibility = View.VISIBLE
        (binding.gifView.drawable as? GifDrawable)?.let {
            it.stop()
            it.startFromFirstFrame()
        }
    }

    override fun startGifVideoFromTheStart() {
        binding.mp4View.visibility = View.VISIBLE
        binding.mp4View.seekTo(0)
        binding.mp4View.start()
    }

    override fun seekAndRestartSound(seekSeconds: Int) {
        soundYouTubePlayer?.let {
            it.pause()
            it.seekTo(seekSeconds.toFloat())
            it.play()
        }
    }

    override fun showYoutubeErrorScreen() {
        // TODO
    }

    override fun setStatusMessage(message: String) {
        binding.statusLabel.text = message
    }

    override fun setVideoOffsetControlsEnabled(enabled: Boolean) {
        binding.addButton.isEnabled = enabled
        binding.decreaseButton.isEnabled = enabled
        binding.refreshButton.isEnabled = enabled
    }

    override fun showRefreshButton() {
        binding.refreshButton.isVisible = true
    }

    override fun showOffsetSeconds(seconds: Int) {
        binding.offsetLabel.isVisible = true
        binding.offsetLabel.text = seconds.toString()
    }

    override fun setShowGIFLayoutVisibitity(visible: Boolean) {
        binding.playGIFLayout.isVisible = visible
    }

    override fun setListener(listener: OpenGSContract.Listener) {
        this.listener = listener
    }

    override fun removeListener(listener: OpenGSContract.Listener) {
        if (this.listener == listener) {
            this.listener = null
        }
    }

    override fun getRoot() = binding.root
}
