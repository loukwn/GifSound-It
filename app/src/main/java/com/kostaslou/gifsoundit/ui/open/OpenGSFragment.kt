package com.kostaslou.gifsoundit.ui.open

import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.kostaslou.gifsoundit.BuildConfig
import com.kostaslou.gifsoundit.R
import com.kostaslou.gifsoundit.ui.base.BaseFragment
import com.kostaslou.gifsoundit.util.GlideApp
import kotlinx.android.synthetic.main.fragment_opengs.*

class OpenGSFragment : BaseFragment() {

    // local vars
    private var gifIsMP4 : Boolean? = null
    private var gifDrawable: GifDrawable? = null

    // ViewModel
    private val viewModel: OpenGSViewModel by lazy {OpenGSViewModel()}

    // setup ui
    override fun layoutRes() = R.layout.fragment_opengs


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init everything
        getArgsFromBundle()
        listenToObservables()
        initUI()
    }

    @Synchronized
    private fun updateStatus(gifSoundPlaybackState: GifSoundPlaybackState, errorMess: String?) {

        val statusText: String
        val gifState = gifSoundPlaybackState.gifState
        val soundState = gifSoundPlaybackState.soundState

        if (gifState==GifSoundPlaybackState.GifState.GIF_OK && soundState==GifSoundPlaybackState.SoundState.SOUND_OK) {
            statusText = "Ready"
        } else {
            // set gif part of message
            val gifPartOfMessage = when (gifState) {
                GifSoundPlaybackState.GifState.GIF_OK -> "ready"
                GifSoundPlaybackState.GifState.GIF_ERROR -> "error" +  if (!TextUtils.isEmpty(errorMess)) ": $errorMess" else ""
                GifSoundPlaybackState.GifState.GIF_LOADING -> "loading"
                GifSoundPlaybackState.GifState.GIF_INVALID -> "invalid"
            }

            // set sound part of message
            val soundPartOfMessage = when (soundState) {
                GifSoundPlaybackState.SoundState.SOUND_OK -> "ready"
                GifSoundPlaybackState.SoundState.SOUND_ERROR -> "error"
                GifSoundPlaybackState.SoundState.SOUND_LOADING -> "loading"
                GifSoundPlaybackState.SoundState.SOUND_INVALID -> "invalid"
            }

            statusText = "GIF: $gifPartOfMessage | Sound: $soundPartOfMessage"
        }

        statusLabel.text = statusText
    }

    private fun initUI() {

        // back
        backButton.setOnClickListener{getBaseActivity()?.onBackPressed()}

        // share
        shareButton.setOnClickListener{
            viewModel.shareButtonClicked()
        }

        // refresh
        refreshButton.setOnClickListener {

            // restart sound
            viewModel.restartSound()

            // restart gif
            if (gifIsMP4 == true) {
                // video view
                mp4View.seekTo(0)
                mp4View.start()

            } else {
                if (gifIsMP4!=null) {
                    // gif
                    gifDrawable?.stop()
                    gifDrawable?.startFromFirstFrame()
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
            if (gifIsMP4 == true) {
                // video view
                mp4View.visibility = View.VISIBLE
                mp4View.start()

            } else {
                if (gifIsMP4 != null) {
                    // show the glide handled gif
                    gifView.visibility = View.VISIBLE
                    gifDrawable?.stop()
                    gifDrawable?.startFromFirstFrame()
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

    private fun initGif(gifLinkState: GifLinkState) {
        val gifLink = gifLinkState.gifLink
        val gifIsMP4 = gifLinkState.gifIsMp4

        if (gifLink!=null) {
            if (gifIsMP4 == false) {
                gifView.setOnClickListener { gifDrawable?.start() }

                GlideApp.with(this)
                        .asGif()
                        .load(gifLink)
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
            } else {
                mp4View.visibility = View.VISIBLE
                mp4View.setOnErrorListener { _, _, extra ->
                    val statusText = when (extra) {
                        MediaPlayer.MEDIA_ERROR_MALFORMED -> "Bitstream is not conforming to the related coding standard or file spec. "
                        MediaPlayer.MEDIA_ERROR_IO -> "IO Error"
                        MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "Not supported by Media framework"
                        MediaPlayer.MEDIA_ERROR_TIMED_OUT -> "Operation timed out"
                        else -> "Unknown Error ($extra)"
                    }

                    viewModel.setGifError(statusText)

                    true
                }

                mp4View.setVideoPath(gifLink)
                mp4View.setOnPreparedListener { mp ->
                    viewModel.setGifOK()

                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                    mp.setVolume(0f,0f)
                }
                mp4View.setOnCompletionListener {
                    mp4View.seekTo(0)
                    mp4View.start()
                }
            }
        }
    }

    private fun initSound(soundLink: String?) {
        if (soundLink!=null) {
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
        offsetLabel.text = resources.getString(R.string.offset_label, seconds)
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
            if (it!=null) startActivity(Intent.createChooser(it, resources.getText(R.string.send_to)))
        })

        // seconds offset changed
        viewModel.secondOffsetLiveData.observe(this, Observer {
            updateOffsetLabel(it)
        })

        // gif link identified
        viewModel.gifLinkStateLiveData.observe(this, Observer {
            initGif(it)
            gifIsMP4 = it.gifIsMp4
        })

        // sound link identified
        viewModel.soundStateLiveData.observe(this, Observer {
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
            if (gifIsMP4 == true) {
                // video view
                mp4View.visibility = View.VISIBLE
                mp4View.start()
                refreshButton.visibility = View.VISIBLE

            } else {
                if (gifIsMP4 != null) {
                    // show the glide handled gif
                    gifView.visibility = View.VISIBLE
                    gifDrawable?.stop()
                    gifDrawable?.startFromFirstFrame()
                    refreshButton.visibility = View.VISIBLE
                }
            }
            offsetLayout.visibility = View.VISIBLE
        })
    }
}