package com.kostaslou.gifsoundit.ui.open

import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
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
import com.kostaslou.gifsoundit.data.disk.SharedPrefsHelper
import com.kostaslou.gifsoundit.ui.base.BaseFragment
import com.kostaslou.gifsoundit.util.GlideApp
import com.kostaslou.gifsoundit.util.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_opengs.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.net.URLDecoder
import javax.inject.Inject

class OpenGSFragment : BaseFragment() {

    private var gifLink : String? = null
    private var soundLink: String? = null
    private var seconds = 0
    private var defaultSeconds = 0

    private var gifReady = false
    private var gifError = false
    private var soundReady = false
    private var soundError = false

    private var canRefresh = false
    private var gifIsMP4 : Boolean? = null
    private lateinit var query : String

    private var hasSeenOffsetTip = false

    private var youTubePlayer: YouTubePlayer? = null
    private var gifDrawable: GifDrawable? = null

    @Inject
    lateinit var sharedPrefsHelper: SharedPrefsHelper

    // ViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: OpenGSViewModel

    // override properties
    override fun name() = "OpenGS"
    override fun layoutRes() = R.layout.fragment_opengs


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(OpenGSViewModel::class.java)

        // init everything
        checkIfThereDialogsToBeShown()
        startSynchronizer()
        getDataFromIntentAndInit()
        initUI()
    }

    private fun startSynchronizer() {
        doAsync {
            while (!gifReady || !soundReady)
                Thread.sleep(200)
            uiThread {
                // play in sync (god bless)
                if (!soundError && !gifError)
                    youTubePlayer?.play()
            }
        }
    }

    @Synchronized
    private fun updateStatus(errorMess: String = "") {

        var statusText = ""

        if (gifLink!=null && soundLink!=null) {

            if (errorMess.isEmpty()) {
                if (gifError) {
                    statusText = "Could not fetch GIF."
                } else {
                    if (!gifReady && soundReady)
                        statusText = "Fetching GIF..."
                    else if (gifReady && !soundReady)
                        statusText = "Fetching Sound..."
                    else if (gifReady && soundReady)
                        statusText = "Ready"
                }
            } else {
                statusText = "GIF Load error: $errorMess"
            }

        } else {
            statusText = if (soundLink==null && gifLink==null) {
                "Could not determine GIF and sound source..."
            } else if (soundLink==null) {
                "Could not determine sound source..."
            } else {
                "Could not determine GIF source..."
            }
        }

        if (statusText.isNotEmpty())
            statusLabel.text = statusText
    }

    private fun initUI() {

        if (soundLink!=null) {
            val videoFrag = childFragmentManager.findFragmentById(R.id.videoView) as YouTubePlayerSupportFragment?
            videoFrag?.initialize(BuildConfig.YouTubeApiKey, object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
                    youTubePlayer = p1
                    youTubePlayer?.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
                    youTubePlayer?.setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener {
                        override fun onSeekTo(p0: Int) {
                        }

                        override fun onBuffering(p0: Boolean) {
                        }

                        override fun onPlaying() {
//                        gifLabel.setText("started")
                            if (!gifError) {
                                if (gifIsMP4 == true) {
                                    // video view
                                    mp4View.visibility = View.VISIBLE
                                    mp4View.start()
                                    canRefresh = true
                                    refreshButton.visibility = View.VISIBLE
                                    updateStatus()

                                } else {
                                    if (gifIsMP4 != null) {
                                        // show the glide handled gif
                                        gifView.visibility = View.VISIBLE
                                        gifDrawable?.stop()
                                        gifDrawable?.startFromFirstFrame()
                                        canRefresh = true
                                        refreshButton.visibility = View.VISIBLE
                                        updateStatus()
                                    }
                                }
                                offsetLayout.visibility = View.VISIBLE
                            }
                        }

                        override fun onStopped() {
                        }

                        override fun onPaused() {
                        }

                    })
                    youTubePlayer?.setPlayerStateChangeListener(object : YouTubePlayer.PlayerStateChangeListener {
                        override fun onAdStarted() {}

                        override fun onLoading() {}

                        override fun onVideoStarted() {
//                        Log.v("VID", "started")

                        }

                        override fun onLoaded(p0: String?) {
//                        Log.v("VID", "loaded")
//                        gifLabel.setText("loaded")
                            soundReady = true
                            updateStatus()
                        }

                        override fun onError(p0: YouTubePlayer.ErrorReason?) {
                            soundError = true
                            soundReady = true
                            setShowGifLabelStatus()
                            updateStatus()
                        }

                        override fun onVideoEnded() {
                            youTubePlayer?.play()
                        }

                    })
                    youTubePlayer?.cueVideo(soundLink, seconds * 1000)
                }

                override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                    soundError = true
                    soundReady = true
                    setShowGifLabelStatus()
                    updateStatus()
                }

            })
        }

        if (gifLink!=null) {
            if (gifIsMP4 == false) {
                if (gifIsMP4 != null) {
                    gifView.setOnClickListener { gifDrawable?.start() }

                    GlideApp.with(this)
                            .asGif()
                            .load(gifLink)
                            .listener(object : RequestListener<GifDrawable> {
                                override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    gifDrawable = resource
                                    gifReady = true
                                    updateStatus()
                                    setShowGifLabelStatus()
                                    return false
                                }

                                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<GifDrawable>?, p3: Boolean): Boolean {
                                    gifReady = true
                                    gifError = true
                                    updateStatus()
                                    return false
                                }
                            })
                            .into(gifView)
                }
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

                    gifReady = true
                    gifError = true
                    updateStatus(statusText)

                    true
                }

                mp4View.setVideoPath(gifLink)
                mp4View.setOnPreparedListener { mp ->
                    //                    Log.v("video", "prepared")
                    gifReady = true
                    setShowGifLabelStatus()
                    updateStatus()
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                    mp.setVolume(0f,0f)
                }
                mp4View.setOnCompletionListener {
                    mp4View.seekTo(0)
                    mp4View.start()
                }
            }
        }

        // button listeners
        // back
        backButton.setOnClickListener{getBaseActivity()?.onBackPressed()}

        // share
        shareButton.setOnClickListener{
            if (!query.startsWith("?"))
                query = "?$query"

            val textToSend = "https://gifsound.com/$query"

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToSend)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.send_to)))
        }

        // refresh
        refreshButton.setOnClickListener {

            // restart sound
            youTubePlayer?.pause()
            youTubePlayer?.seekToMillis(seconds * 1000)
            youTubePlayer?.play()

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
            if (!hasSeenOffsetTip) {
                offsetTip.visibility = View.VISIBLE

                sharedPrefsHelper.put("hasSeenOffsetTip", true)

                hasSeenOffsetTip = true
            }
            seconds += 1
            updateOffsetLabel()
        }

        // remove seconds
        removeButton.setOnClickListener {
            if (!hasSeenOffsetTip) {
                offsetTip.visibility = View.VISIBLE

                sharedPrefsHelper.put("hasSeenOffsetTip", true)

                hasSeenOffsetTip = true
            }
            seconds = if (seconds>0) seconds -1 else 0
            updateOffsetLabel()
        }

        // revert to default seconds
        revertButton.setOnClickListener {
            seconds = defaultSeconds
            updateOffsetLabel()
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

        // offset tip
        offsetTip.setOnClickListener {
            offsetTip.visibility = View.GONE
        }


        // change typeface of textviews
        getBaseActivity()?.let {
            val typeFace = Typeface.createFromAsset(it.assets, "fonts/pricedown.ttf")
            gifLabel.typeface = typeFace
            soundLabel.typeface = typeFace
            offsetLabel.typeface = typeFace
        }
        statusLabel.isSelected = true
    }

    @Synchronized
    private fun setShowGifLabelStatus() {
        if (gifReady && soundError && !gifError) {
            playGIFLayout.visibility = View.VISIBLE
        }
    }

    private fun updateOffsetLabel() {
        offsetLabel.text = resources.getString(R.string.offset_label, seconds)
    }

    private fun getDataFromIntentAndInit() {

        arguments?.let {
            it.getString("query")?.let { q ->
                setGifSoundArgs(q)
            }
        }
    }

    private fun setGifSoundArgs(query: String) {
        // save the query so we can share it if we want
        this.query = query
        Timber.d(query)
        updateOffsetLabel()

        // loop query args and save them
        val args = query.split("&")
        for (arg in args) {

            // youtube
            if (arg.startsWith("v=")) {
                soundLink = arg.split("=")[1]
                continue
            }

            // youtube #2
            if (arg.startsWith("sound=")) {
                soundLink = URLDecoder.decode(arg, "UTF-8")

                val temp = soundLink ?: return
                soundLink = URLDecoder.decode(temp.split("=")[2], "UTF-8")

                continue
            }

            // second offset
            if (arg.startsWith("s=")) {
                seconds = arg.split("=")[1].toInt()
                defaultSeconds = seconds
                updateOffsetLabel()
                continue
            }

            // second offset #2
            if (arg.startsWith("start=")) {
                seconds = arg.split("=")[1].toInt()
                defaultSeconds = seconds
                updateOffsetLabel()
                continue
            }

            // imgur gif #1
            if (arg.startsWith("gifv=")) {
                gifLink = "http://i.imgur.com/" + arg.split("=")[1] + ".mp4"
                gifIsMP4 = true
                continue
            }

            // imgur gif #2
            if (arg.startsWith("mp4")) {
                gifLink = arg.split("=")[1] + ".mp4"
                gifIsMP4 = true
                continue
            }

            // gfycat gif
            if (arg.startsWith("gfycat")) {
                gifLink = "https://giant.gfycat.com/" + arg.split("=")[1] + ".mp4"
                gifIsMP4 = true
                continue
            }

            // normal gif
            if (arg.startsWith("gif=")) {
                gifLink = URLDecoder.decode(arg.split("=")[1], "UTF-8")

                val temp = gifLink ?: return
                if (!temp.startsWith("http") && !temp.startsWith("https"))
                    gifLink = "http://$temp"

                gifIsMP4 = false
                continue
            }

            // webm
            if (arg.startsWith("webm=")) {
                gifLink = URLDecoder.decode(arg.split("=")[1], "UTF-8") + ".webm"

                val temp = gifLink ?: return
                if (!temp.startsWith("http") && !temp.startsWith("https"))
                    gifLink = "http://$temp"

                gifIsMP4 = true
                continue
            }
        }
    }

    private fun checkIfThereDialogsToBeShown() {

        // first we will get some vars from sharedPreferences

        hasSeenOffsetTip = sharedPrefsHelper["hasSeenOffsetTip", false] ?: false
//        val prefs = getSharedPreferences("dialog_stuff", Context.MODE_PRIVATE)
//        hasSeenOffsetTip = prefs.getBoolean("hasSeenOffsetTip", false)

    }
}