package com.kostaslou.gifsoundit.opengs

import com.kostaslou.gifsoundit.common.contract.ActionableViewContract
import com.kostaslou.gifsoundit.opengs.controller.GifState
import com.kostaslou.gifsoundit.opengs.controller.OpenGSUIModel
import com.kostaslou.gifsoundit.opengs.controller.SoundState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

interface OpenGSMvc {

    interface Listener {
        fun onBackButtonPressed()
        fun onRefreshButtonPressed()
        fun onShareButtonPressed()
        fun onPlayGifLabelPressed()
        fun onOffsetIncreaseButtonPressed()
        fun onOffsetDecreaseButtonPressed()
        fun onOffsetResetButtonPressed()
        fun onGifStateChanged(gifState: GifState)
        fun onSoundStateChanged(soundState: SoundState)
    }

    interface View : ActionableViewContract<Listener> {
        fun initView(
            uiModel: OpenGSUIModel,
            soundYouTubeListener: AbstractYouTubePlayerListener
        )

        fun getSoundGifView(): YouTubePlayerView
        fun startGifFromTheStart(uiModel: OpenGSUIModel)
        fun showGIFPlayLayout()
        fun updateStatusMessages(uiModel: OpenGSUIModel)
    }
}