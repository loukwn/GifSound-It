package com.kostaslou.gifsoundit.opengs.view

import android.content.Context
import android.media.MediaPlayer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gifsoundit.opengs.R
import com.kostaslou.gifsoundit.opengs.OpenGSMvc
import com.kostaslou.gifsoundit.opengs.controller.GifSource
import com.kostaslou.gifsoundit.opengs.controller.GifState
import com.kostaslou.gifsoundit.opengs.controller.OpenGSUIModel
import com.kostaslou.gifsoundit.opengs.controller.SoundSource
import com.kostaslou.gifsoundit.opengs.controller.SoundState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.fragment_opengs.view.*

class OpenGSViewMvcImpl(
    val context: Context,
    inflater: LayoutInflater,
    container: ViewGroup?
) : OpenGSMvc.View {

    private var listener: OpenGSMvc.Listener? = null
    private val view = inflater.inflate(R.layout.fragment_opengs, container, false)
    private var gifDrawable: GifDrawable? = null

    init {
        setupViewAttributes()
        setupClickListeners()
    }

    private fun setupViewAttributes() {

        // for the marquee
        view.statusLabel.isSelected = true
    }

    private fun setupClickListeners() {
        view.backButton.setOnClickListener {
            listener?.onBackButtonPressed()
        }

        view.refreshButton.setOnClickListener {
            listener?.onRefreshButtonPressed()
        }

        view.shareButton.setOnClickListener {
            listener?.onShareButtonPressed()
        }

        view.playGIFLabel.setOnClickListener {
            listener?.onPlayGifLabelPressed()
        }

        view.gifLabelCloseButton.setOnClickListener {
            view.playGIFLayout.visibility = View.GONE
        }

        view.addButton.setOnClickListener {
            listener?.onOffsetIncreaseButtonPressed()
        }

        view.decreaseButton.setOnClickListener {
            listener?.onOffsetDecreaseButtonPressed()
        }

        view.revertButton.setOnClickListener {
            listener?.onOffsetResetButtonPressed()
        }
    }

    override fun initView(
        uiModel: OpenGSUIModel,
        soundYouTubeListener: AbstractYouTubePlayerListener
    ) {
        setGifStatusText(uiModel.gifState)
        setSoundStatusText(uiModel.soundState)

        initGif(uiModel.gifSource)
        initSound(uiModel.soundSource, soundYouTubeListener)
    }

    private fun initGif(gifSource: GifSource) {
        val gifLink = gifSource.gifLink
        val gifType = gifSource.gifType

        gifLink?.let {

            view.gifView.visibility = View.INVISIBLE
            view.mp4View.visibility = View.INVISIBLE

            when (gifType) {
                GifSource.GifType.GIF -> {
                    // the gif is actually a gif, so we use glide

                    view.gifView.setOnClickListener { gifDrawable?.start() }

                    Glide.with(context)
                        .asGif()
                        .load(it)
                        .listener(object : RequestListener<GifDrawable> {
                            override fun onResourceReady(
                                resource: GifDrawable?,
                                model: Any?,
                                target: Target<GifDrawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                gifDrawable = resource
                                listener?.onGifStateChanged(GifState.GIF_OK)
                                setGifStatusText(GifState.GIF_OK)
                                return false
                            }

                            override fun onLoadFailed(
                                p0: GlideException?,
                                p1: Any?,
                                p2: Target<GifDrawable>?,
                                p3: Boolean
                            ): Boolean {
                                listener?.onGifStateChanged(GifState.GIF_ERROR)
                                setGifStatusText(GifState.GIF_ERROR)
                                return false
                            }
                        })
                        .into(view.gifView)
                }
                GifSource.GifType.MP4 -> {
                    // the gif is actually an mp4, so we use mp4view

                    view.mp4View.visibility = View.VISIBLE
                    view.mp4View.setOnErrorListener { _, _, extra ->
                        val statusText = when (extra) {
                            MediaPlayer.MEDIA_ERROR_MALFORMED -> getString(R.string.opengs_error_gif_malformed)
                            MediaPlayer.MEDIA_ERROR_IO -> getString(R.string.opengs_error_gif_io_error)
                            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> getString(R.string.opengs_error_gif_unsupported)
                            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> getString(R.string.opengs_error_gif_timed_out)
                            else -> getString(
                                R.string.opengs_error_gif_unknown_with_code,
                                extra.toString()
                            )
                        }

                        listener?.onGifStateChanged(GifState.GIF_ERROR)
                        setGifStatusText(GifState.GIF_ERROR, statusText)

                        true
                    }

                    view.mp4View.setVideoPath(it)
                    view.mp4View.setOnPreparedListener { mp ->
                        listener?.onGifStateChanged(GifState.GIF_OK)
                        setGifStatusText(GifState.GIF_OK)

                        mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                        mp.setVolume(0f, 0f)
                    }
                    view.mp4View.setOnCompletionListener {
                        view.mp4View.seekTo(0)
                        view.mp4View.start()
                    }
                }
            }
        }
    }

    private fun initSound(
        soundSource: SoundSource,
        soundYouTubeListener: AbstractYouTubePlayerListener
    ) {
        soundSource.soundPart?.let {
            // setup android youtube player

            view.soundView.addYouTubePlayerListener(soundYouTubeListener)
        }
    }

    private fun setGifStatusText(gifState: GifState, message: String = "") {
        val statusMessage = when (gifState) {
            GifState.GIF_OK -> getString(R.string.opengs_one_ready)
            GifState.GIF_ERROR -> getString(R.string.opengs_one_error) +
                if (!TextUtils.isEmpty(message))
                    ": $message"
                else
                    ": ${getString(R.string.opengs_error_gif_unknown_with)}"
            GifState.GIF_LOADING -> getString(R.string.opengs_one_loading)
            GifState.GIF_INVALID -> getString(R.string.opengs_one_invalid)
        }
        view.statusLabel.text = statusMessage
    }

    private fun setSoundStatusText(soundState: SoundState) {
        val statusMessage = when (soundState) {
            SoundState.SOUND_OK -> getString(R.string.opengs_one_ready)
            SoundState.SOUND_ERROR -> getString(R.string.opengs_one_error)
            SoundState.SOUND_LOADING -> getString(R.string.opengs_one_loading)
            SoundState.SOUND_INVALID -> getString(R.string.opengs_one_invalid)
        }

        view.statusLabel.text = statusMessage
    }

    override fun startGifFromTheStart(uiModel: OpenGSUIModel) {
        when (uiModel.gifSource.gifType) {
            GifSource.GifType.GIF -> {
                view.gifView.visibility = View.VISIBLE
                gifDrawable?.stop()
                gifDrawable?.startFromFirstFrame()
            }
            GifSource.GifType.MP4 -> {
                view.mp4View.visibility = View.VISIBLE
                view.mp4View.seekTo(0)
                view.mp4View.start()
            }
        }

        // show refresh button
        view.refreshButton.visibility = View.VISIBLE

        // set offset seconds
        view.offsetLabel.text = uiModel.secondsVideoOffset.toString()
    }

    override fun showGIFPlayLayout() {
        view.playGIFLayout.visibility = View.VISIBLE
    }

    override fun updateStatusMessages(uiModel: OpenGSUIModel) {
        setGifStatusText(gifState = uiModel.gifState)
        setSoundStatusText(soundState = uiModel.soundState)
    }

    override fun setListener(listener: OpenGSMvc.Listener) {
        this.listener = listener
    }

    override fun removeListener(listener: OpenGSMvc.Listener) {
        if (this.listener == listener) {
            this.listener = null
        }
    }

    override fun getSoundGifView(): YouTubePlayerView {
        return view.soundView
    }

    override fun getRootView(): View {
        return view
    }
}

fun OpenGSViewMvcImpl.getString(@StringRes resId: Int): String {
    return context.getString(resId)
}

fun OpenGSViewMvcImpl.getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
    return context.getString(resId, *formatArgs)
}
