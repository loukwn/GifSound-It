package com.kostaslou.gifsoundit

import android.content.SharedPreferences
import com.kostaslou.gifsoundit.api.RestAPI
import com.kostaslou.gifsoundit.commons.LocalPostData
import com.kostaslou.gifsoundit.commons.PostModel
import rx.Observable
import java.util.*

class MainPresenter(private val sharedPreferences : SharedPreferences, private val api: RestAPI = RestAPI()) {

    // access token
    fun getAccessToken(): Observable<String> {
        return Observable.create {
            subscriber ->
            val callResponse = api.getAuthToken()
            val response = callResponse.execute()

            val accessToken : String?

            if (response.isSuccessful) {
                accessToken = response.body()?.access_token
                val expiresIn = response.body()?.expires_in

                // update shared preferences
                with (sharedPreferences.edit()) {
                    if ((accessToken ?: "").isNotEmpty()) {
                        putString("access_token", accessToken ?: "")
                        if (expiresIn != null) {
                            val date = Date(Date().time + expiresIn.toLong() * 1000)
                            putLong("expires_in_date", date.time)
                        } else {
                            putLong("expires_in_date", Date().time)
                        }
                    }
                    apply()
                }

                subscriber.onNext(accessToken)
            }
            subscriber.onCompleted()
        }
    }

    // posts
    fun getPosts(after: String, postType: Int, topType: String = "all"): Observable<LocalPostData> {

        val accessToken : String = sharedPreferences.getString("access_token", "") ?: ""

        return Observable.create {
            subscriber ->
            val callResponse = api.getPosts(accessToken, postType, after, topType)
            val response = callResponse?.execute()

            if (response?.isSuccessful == true) {
                // response is successfull, we loop the results
                val postData = response.body()?.data

                val posts = postData?.children?.map {
                    val item = it.data

                    val url = if (item.url.startsWith("/r")) "http://www.reddit.com"+item.url else item.url
                    val perma = if (item.permalink.startsWith("/r")) "http://www.reddit.com"+item.permalink else item.permalink
                    val created = item.created_utc
                    val score = item.score
                    val isSelf = item.is_self

                    PostModel(item.title, item.thumbnail, created, score, url, perma, isSelf)
                }

                if (posts!=null) {
                    val toReturn = LocalPostData(
                            posts,
                            postData.before ?: "",
                            postData.after ?: "")

                    subscriber.onNext(toReturn)
                    subscriber.onCompleted()
                } else {
                    subscriber.onError(Throwable(response.message()))
                }
            } else {
                subscriber.onError(Throwable(response?.message()))
            }
        }
    }
}