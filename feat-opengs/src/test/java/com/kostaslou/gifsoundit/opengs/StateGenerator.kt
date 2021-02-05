package com.kostaslou.gifsoundit.opengs

import com.kostaslou.gifsoundit.common.util.Event

internal fun State.Companion.default(
    gifSource: GifSource = GifSource("", GifType.GIF),
    soundSource: SoundSource = SoundSource("", 0),
    gifState: GifState = GifState.GIF_LOADING,
    soundState: SoundState = SoundState.SOUND_LOADING,
    gifAction: Event<PlaybackAction> = Event(PlaybackAction.PREPARE),
    soundAction: Event<PlaybackAction> = Event(PlaybackAction.PREPARE),
    currentSecondsOffset: Int = 0,
    isFromDeepLink: Boolean = false,
) = State(
    gifSource,
    soundSource,
    gifState,
    soundState,
    gifAction,
    soundAction,
    currentSecondsOffset,
    isFromDeepLink
)
