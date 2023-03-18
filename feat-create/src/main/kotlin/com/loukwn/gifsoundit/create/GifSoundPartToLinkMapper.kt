package com.loukwn.gifsoundit.create

import androidx.annotation.VisibleForTesting
import javax.inject.Inject

class GifSoundPartToLinkMapper @Inject constructor() {
    fun map(gifLink: String, videoLink: String, secondsOffset: Int): String {
        val firstPartOfLink = "https://gifsound.com/?"
        val videoPartOfLink = "&v=$videoLink"
        val secondsPartOfLink = if (secondsOffset > 0) {
            "s=$secondsOffset"
        } else {
            ""
        }

        val gifPartOfLink = getGifPart(gifLink)

        return "$firstPartOfLink$gifPartOfLink$videoPartOfLink$secondsPartOfLink"
    }

    @VisibleForTesting
    fun getGifPart(gifLink: String): String {
        return when {
            gifLink.startsWith("https://imgur.com/") ||
            gifLink.startsWith("https://i.imgur.com/")-> {
                val gifVCode = gifLink.split("/")[3].split(".")[0]
                "gifv=$gifVCode"
            }
            gifLink.startsWith("https://gfycat.com/") -> {
                val gfycatCode = gifLink.split("/")[3].split(".")[0]
                "gfycat=$gfycatCode"
            }
            gifLink.contains(".mp4") -> "mp4=$gifLink"
            gifLink.contains(".webm") -> "webm=$gifLink"
            else -> "gif=$gifLink"
        }
    }
}
