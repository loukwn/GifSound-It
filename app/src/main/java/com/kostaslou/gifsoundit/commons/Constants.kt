package com.kostaslou.gifsoundit.commons

// whether a recycler row is post or a loading item, so that the appropriate adapter will be chosen
object AdapterConstants {
    const val POSTS = 1
    const val LOADING = 2
}

object PostType {
    const val HOT = 1
    const val TOP = 2
    const val NEW = 3
}

// some reddit related constants
object RedditConstants {
//    const val CLIENT_ID = "rR6ycz_KdFvt4g"
    const val USER_AGENT = "GifSound It by loukwn"
    const val TOKEN_TYPE = "access_token"
    const val NUM_OF_POSTS_PER_REQUEST = 25
}

object GeneralConstants {
    const val MINUTES_TO_REFRESH = 5
}