package com.kostaslou.gifsoundit.opengs

import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gifsoundit.opengs.BuildConfig
import com.gifsoundit.opengs.R
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.kostaslou.gifsoundit.common.BaseFragment
import com.kostaslou.gifsoundit.opengs.util.GifSoundPlaybackState
import com.kostaslou.gifsoundit.opengs.util.GifUrl
import com.kostaslou.gifsoundit.opengs.util.SoundUrl
import kotlinx.android.synthetic.main.fragment_opengs.*

class OpenGSFragment : BaseFragment() {

    // local vars
    private var gifType = GifUrl.GifType.GIF
    private var gifDrawable: GifDrawable? = null

    // ViewModel
    private val viewModel: OpenGSViewModel by lazy { OpenGSViewModel() }

    // setup ui
    override fun layoutRes() = R.layout.fragment_opengs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init everything
        getArgsFromBundle()
        initUI()
        listenToObservables()
    }

    @Synchronized
    private fun updateStatus(gifSoundPlaybackState: GifSoundPlaybackState, errorMess: String?) {

        val statusText: String
        val gifState = gifSoundPlaybackState.gifState
        val soundState = gifSoundPlaybackState.soundState

        if (gifState == GifSoundPlaybackState.GifState.GIF_OK && soundState == GifSoundPlaybackState.SoundState.SOUND_OK) {
            statusText = getString(R.string.opengs_all_ready)
        } else {
            // set gif part of message
            val gifPartOfMessage = when (gifState) {
                GifSoundPlaybackState.GifState.GIF_OK -> getString(R.string.opengs_one_ready)
                GifSoundPlaybackState.GifState.GIF_ERROR -> getString(R.string.opengs_one_error) + if (!TextUtils.isEmpty(errorMess)) ": $errorMess" else ""
                GifSoundPlaybackState.GifState.GIF_LOADING -> getString(R.string.opengs_one_loading)
                GifSoundPlaybackState.GifState.GIF_INVALID -> getString(R.string.opengs_one_invalid)
            }

            // set sound part of message
            val soundPartOfMessage = when (soundState) {
                GifSoundPlaybackState.SoundState.SOUND_OK -> getString(R.string.opengs_one_ready)
                GifSoundPlaybackState.SoundState.SOUND_ERROR -> getString(R.string.opengs_one_error)
                GifSoundPlaybackState.SoundState.SOUND_LOADING -> getString(R.string.opengs_one_loading)
                GifSoundPlaybackState.SoundState.SOUND_INVALID -> getString(R.string.opengs_one_invalid)
            }

            statusText = "GIF: $gifPartOfMessage | Sound: $soundPartOfMessage"
        }

        statusLabel.text = statusText
    }

    private fun initUI() {

        // back
        backButton.setOnClickListener { getBaseActivity()?.onBackPressed() }

        // share
        shareButton.setOnClickListener {
            viewModel.shareButtonClicked()
        }

        // refresh
        refreshButton.setOnClickListener {

            // restart sound
            viewModel.restartSound()

            // restart gif
            when (gifType) {
                GifUrl.GifType.GIF -> {
                    // gif
                    gifDrawable?.stop()
                    gifDrawable?.startFromFirstFrame()
                }
                GifUrl.GifType.MP4 -> {
                    // video view
                    mp4View.seekTo(0)
                    mp4View.start()
                }
            }
        }

        // add seconds
        addButton.setOnClickListener {
            viewModel.addButtonClicked()
        }

        // remove seconds
        decreaseButton.setOnClickListener {
            viewModel.decreaseButtonClicked()
        }

        // revert to default seconds
        revertButton.setOnClickListener {
            viewModel.revertButtonClicked()
        }

        // show GIF label
        playGIFLabel.setOnClickListener {
            when (gifType) {
                GifUrl.GifType.GIF -> {
                    // show the glide handled gif
                    gifView.visibility = View.VISIBLE
                    gifDrawable?.stop()
                    gifDrawable?.startFromFirstFrame()
                }
                GifUrl.GifType.MP4 -> {
                    // video view
                    mp4View.visibility = View.VISIBLE
                    mp4View.start()
                }
            }
        }

        // gif label layout close button
        gifLabelCloseButton.setOnClickListener {
            playGIFLayout.visibility = View.GONE
        }

        // change typeface of textviews
        getBaseActivity()?.let {
            val typeFace = Typeface.createFromAsset(it.assets, "fonts/pricedown.ttf")
            gifLabel.typeface = typeFace
            soundLabel.typeface = typeFace
            offsetLabel.typeface = typeFace
        }

        // for the marquee
        statusLabel.isSelected = true
    }

    private fun initGif(gifUrl: GifUrl) {
        val gifLink = gifUrl.gifLink
        val gifType = gifUrl.gifType

        gifLink?.let {

            when (gifType) {
                GifUrl.GifType.GIF -> {
                    // the gif is actually a gif, so we use glide

                    gifView.setOnClickListener { gifDrawable?.start() }

                    Glide.with(this)
                            .asGif()
                            .load(it)
                            .listener(object : RequestListener<GifDrawable> {
                                override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    gifDrawable = resource
                                    viewModel.setGifOK()
                                    return false
                                }

                                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<GifDrawable>?, p3: Boolean): Boolean {
                                    viewModel.setGifError()
                                    return false
                                }
                            })
                            .into(gifView)
                }
                GifUrl.GifType.MP4 -> {
                    // the gif is actually an mp4, so we use mp4view

                    mp4View.visibility = View.VISIBLE
                    mp4View.setOnErrorListener { _, _, extra ->
                        val statusText = when (extra) {
                            MediaPlayer.MEDIA_ERROR_MALFORMED -> getString(R.string.opengs_error_gif_malformed)
                            MediaPlayer.MEDIA_ERROR_IO -> getString(R.string.opengs_error_gif_io_error)
                            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> getString(R.string.opengs_error_gif_unsupported)
                            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> getString(R.string.opengs_error_gif_timed_out)
                            else -> getString(R.string.opengs_error_gif_unknown, extra.toString())
                        }

                        viewModel.setGifError(statusText)

                        true
                    }

                    mp4View.setVideoPath(it)
                    mp4View.setOnPreparedListener { mp ->
                        viewModel.setGifOK()

                        mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                        mp.setVolume(0f, 0f)
                    }
                    mp4View.setOnCompletionListener {
                        mp4View.seekTo(0)
                        mp4View.start()
                    }
                }
            }
        }
    }

    private fun initSound(soundUrl: SoundUrl) {
        soundUrl.soundLink?.let {
            val videoFrag = childFragmentManager.findFragmentById(R.id.videoView) as YouTubePlayerSupportFragment?
            videoFrag?.initialize(BuildConfig.YouTubeApiKey, object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
                    viewModel.setYoutubePlayer(p1)
                }

                override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                    viewModel.setSoundError()
                }
            })
        }
    }

    private fun setShowGifLabelStatus(gifSoundPlaybackState: GifSoundPlaybackState) {
        // if gif is ok and sound is either error or invalid, give the option to play it.

        if (gifSoundPlaybackState.gifState == GifSoundPlaybackState.GifState.GIF_OK &&
                (gifSoundPlaybackState.soundState == GifSoundPlaybackState.SoundState.SOUND_ERROR ||
                        gifSoundPlaybackState.soundState == GifSoundPlaybackState.SoundState.SOUND_INVALID)) {
            playGIFLayout.visibility = View.VISIBLE
        }
    }

    private fun updateOffsetLabel(seconds: Int) {
        offsetLabel.text = getString(R.string.opengs_label_offset, seconds)
    }

    private fun getArgsFromBundle() {

        arguments?.let {
            it.getString("query")?.let { q ->
                viewModel.setGifSoundArgs(q)
            }
        }
    }

    private fun listenToObservables() {

        // share gifsound
        viewModel.shareIntentLiveData.observe(this, Observer {
            if (it != null) startActivity(Intent.createChooser(it, resources.getText(R.string.opengs_send_to)))
        })

        // seconds offset changed
        viewModel.secondOffsetLiveData.observe(this, Observer {
            updateOffsetLabel(it)
        })

        // gif link identified
        viewModel.gifUrlLiveData.observe(this, Observer {
            initGif(it)
            gifType = it.gifType
        })

        // sound link identified
        viewModel.soundUrlLiveData.observe(this, Observer {
            initSound(it)
        })

        // gifsound state changed
        viewModel.gifSoundStateLiveData.observe(this, Observer {
            val gifState = it.gifState
            val soundState = it.soundState

            updateStatus(it, it.errorMessage)

            if (gifState == GifSoundPlaybackState.GifState.GIF_OK)
                setShowGifLabelStatus(it)

            if (gifState == GifSoundPlaybackState.GifState.GIF_OK && soundState == GifSoundPlaybackState.SoundState.SOUND_OK) {
                viewModel.startGifSound()
            }
        })

        // sound started so gif should start too
        viewModel.startGifLiveData.observe(this, Observer {
            when (gifType) {
                GifUrl.GifType.GIF -> {
                    // show the glide handled gif
                    gifView.visibility = View.VISIBLE
                    gifDrawable?.stop()
                    gifDrawable?.startFromFirstFrame()
                    refreshButton.visibility = View.VISIBLE
                }
                GifUrl.GifType.MP4 -> {
                    // video view
                    mp4View.visibility = View.VISIBLE
                    mp4View.start()
                    refreshButton.visibility = View.VISIBLE
                }
            }

            offsetLayout.visibility = View.VISIBLE
        })
    }
}
