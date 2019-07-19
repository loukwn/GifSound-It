package com.kostaslou.gifsoundit.commons

// whether a recycler row is post or a loading item, so that the appropriate adapter will be chosen
object AdapterConstants {
    const val POSTS = 1
    const val LOADING = 2
}

enum class PostType {
    HOT, TOP, NEW
}

// some reddit related constants
object RedditConstants {
    const val REDDIT_AUTH_BASE_URL = "https://www.reddit.com/"
    const val REDDIT_POST_BASE_URL = "https://www.oauth.reddit.com/"
    const val REDDIT_GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client"
    const val NUM_OF_POSTS_PER_REQUEST = 25
}

object GeneralConstants {
    const val MINUTES_TO_REFRESH = 5
    const val AMOUNT_OF_VIEWS_TO_INSTA_SCROLL = 60
}