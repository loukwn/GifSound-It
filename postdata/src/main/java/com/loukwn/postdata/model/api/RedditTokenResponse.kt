package com.loukwn.postdata.model.api

import androidx.annotation.Keep

@Keep
internal class RedditTokenResponse(
    val access_token: String,
    val expires_in: String
)
