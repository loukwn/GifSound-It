package com.kostaslou.gifsoundit

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.kostaslou.gifsoundit.util.GlideApp
import kotlinx.android.synthetic.main.activity_open_gs.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URLDecoder


class OpenGSActivity : YouTubeBaseActivity() {

    private var gifLink : String? = null
    private var vidLink : String? = null
    private var seconds = 0
    private var defaultSeconds = 0

    private var gifReady = false
    private var vidReady = false
    private var vidError = false
    private var gifError = false

    private var canRefresh = false
    private var gifIsVideo : Boolean? = null
    private lateinit var query : String

    private var hasSeenOffsetTip = false

    private var youTubePlayer: YouTubePlayer? = null
    private var gifDrawable: GifDrawable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_gs)

        // init everything
        checkIfThereDialogsToBeShown()
        startSynchronizer()
        getDataFromIntentAndInit()
        initUI()
    }

    private fun startSynchronizer() {
        doAsync {
            while (!gifReady || !vidReady)
                Thread.sleep(200)
            uiThread {
                // play in sync (god bless)
                if (!vidError && !gifError)
                    youTubePlayer?.play()
            }
        }
    }

    @Synchronized
    private fun updateStatus(errorMess: String = "") {

        var statusText = ""

        if (gifLink!=null && vidLink!=null) {

            if (errorMess.isEmpty()) {
                if (gifError) {
                    statusText = "Could not fetch GIF..."
                } else {
                    if (!gifReady && vidReady)
                        statusText = "Fetching GIF..."
                    else if (gifReady && !vidReady)
                        statusText = "Fetching Sound..."
                    else if (gifReady && vidReady)
                        statusText = "Ready"
                }
            } else {
                statusText = "GIF Load error: $errorMess"
            }

        } else {
            statusText = if (vidLink==null && gifLink==null) {
                "Could not determine GIF and sound source..."
            } else if (vidLink==null) {
                "Could not determine sound source..."
            } else {
                "Could not determine GIF source..."
            }
        }

        if (statusText.isNotEmpty())
        statusLabel.text = statusText
    }

    private fun initUI() {

        if (vidLink!=null) {
            videoView.initialize(BuildConfig.YouTubeApiKey, object : YouTubePlayer.OnInitializedListener {
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
                                if (gifIsVideo == true) {
                                    // video view
                                    mp4View.visibility = View.VISIBLE
                                    mp4View.start()
                                    canRefresh = true
                                    refreshButton.visibility = View.VISIBLE
                                    updateStatus()

                                } else {
                                    if (gifIsVideo != null) {
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
                            vidReady = true
                            updateStatus()
                        }

                        override fun onError(p0: YouTubePlayer.ErrorReason?) {
                            vidError = true
                            vidReady = true
                            setShowGifLabelStatus()
                            updateStatus()
                        }

                        override fun onVideoEnded() {
                            youTubePlayer?.play()
                        }

                    })
                    youTubePlayer?.cueVideo(vidLink, seconds * 1000)
                }

                override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                    vidError = true
                    vidReady = true
                    setShowGifLabelStatus()
                    updateStatus()
                }

            })
        }

        if (gifLink!=null) {
            if (gifIsVideo == false) {
                if (gifIsVideo != null) {
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
                        else -> "Unknown Error (" + extra.toString() + ")"
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
        backButton.setOnClickListener{onBackPressed()}

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
            if (gifIsVideo == true) {
                // video view
                mp4View.seekTo(0)
                mp4View.start()

            } else {
                if (gifIsVideo!=null) {
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
                val prefs = getSharedPreferences("dialog_stuff", Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean("hasSeenOffsetTip", true)
                editor.apply()
                hasSeenOffsetTip = true
            }
            seconds += 1
            updateOffsetLabel()
        }

        // remove seconds
        removeButton.setOnClickListener {
            if (!hasSeenOffsetTip) {
                offsetTip.visibility = View.VISIBLE
                val prefs = getSharedPreferences("dialog_stuff", Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean("hasSeenOffsetTip", true)
                editor.apply()
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
            if (gifIsVideo == true) {
                // video view
                mp4View.visibility = View.VISIBLE
                mp4View.start()

            } else {
                if (gifIsVideo != null) {
                    // show the glide handled gif
                    gifView.visibility = View.VISIBLE
                    gifDrawable?.stop()
                    gifDrawable?.startFromFirstFrame()
                }
            }
        }

        // offset tip
        offsetTip.setOnClickListener {
            offsetTip.visibility = View.GONE
        }


        // change typeface of textviews
        val typeFace = Typeface.createFromAsset(assets, "fonts/pricedown.ttf")
        gifLabel.typeface = typeFace
        soundLabel.typeface = typeFace
        offsetLabel.typeface = typeFace

        statusLabel.isSelected = true
    }

    @Synchronized
    private fun setShowGifLabelStatus() {
        if (gifReady && vidError && !gifError) {
            playGIFLabel.visibility = View.VISIBLE
        }
    }

    private fun updateOffsetLabel() {
        offsetLabel.text = resources.getString(R.string.offset_label, seconds)
    }

    private fun getDataFromIntentAndInit() {

        if (intent?.extras?.containsKey("query") == true) {
            // if we come from a previous activity
            setGifSoundArgs(intent.extras.getString("query"))
        } else if (intent?.data?.query?.isNotEmpty() == true) {
            // if we come from another app with data
            setGifSoundArgs(intent.data.query)
        }
    }

    private fun setGifSoundArgs(query: String) {
        // save the query so we can share it if we want
        this.query = query
        updateOffsetLabel()

        // loop query args and save them
        val args = query.split("&")
        for (arg in args) {

            // youtube
            if (arg.startsWith("v=")) {
                vidLink = arg.split("=")[1]
                continue
            }

            // youtube #2
            if (arg.startsWith("sound=")) {
                vidLink = URLDecoder.decode(arg, "UTF-8")

                val temp = vidLink ?: return
                vidLink = URLDecoder.decode(temp.split("=")[2], "UTF-8")

                continue
            }

            // second offset
            if (arg.startsWith("s=")) {
                seconds = arg.split("=")[1].toInt() + 1
                defaultSeconds = seconds
                updateOffsetLabel()
                continue
            }

            // second offset #2
            if (arg.startsWith("start=")) {
                seconds = arg.split("=")[1].toInt() + 1
                defaultSeconds = seconds
                updateOffsetLabel()
                continue
            }

            // imgur gif #1
            if (arg.startsWith("gifv=")) {
                gifLink = "http://i.imgur.com/" + arg.split("=")[1] + ".mp4"
                gifIsVideo = true
                continue
            }

            // imgur gif #2
            if (arg.startsWith("mp4")) {
                gifLink = arg.split("=")[1] + ".mp4"
                gifIsVideo = true
                continue
            }

            // gfycat gif
            if (arg.startsWith("gfycat")) {
                gifLink = "https://giant.gfycat.com/" + arg.split("=")[1] + ".mp4"
                gifIsVideo = true
                continue
            }

            // normal gif
            if (arg.startsWith("gif=")) {
                gifLink = URLDecoder.decode(arg.split("=")[1], "UTF-8")

                val temp = gifLink ?: return
                if (!temp.startsWith("http") && !temp.startsWith("https"))
                    gifLink = "http://$temp"

                gifIsVideo = false
                continue
            }

            // webm
            if (arg.startsWith("webm=")) {
                gifLink = URLDecoder.decode(arg.split("=")[1], "UTF-8") + ".webm"

                val temp = gifLink ?: return
                if (!temp.startsWith("http") && !temp.startsWith("https"))
                    gifLink = "http://$temp"

                gifIsVideo = true
                continue
            }
        }
    }

    private fun checkIfThereDialogsToBeShown() {

        // first we will get some vars from sharedPreferences
        val prefs = getSharedPreferences("dialog_stuff", Context.MODE_PRIVATE)
        hasSeenOffsetTip = prefs.getBoolean("hasSeenOffsetTip", false)

    }
}