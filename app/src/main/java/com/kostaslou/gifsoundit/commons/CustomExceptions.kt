package com.kostaslou.gifsoundit.commons

import retrofit2.HttpException

class TokenHttpException(val httpException: HttpException) : Exception(httpException)

class PostsHttpException(val httpException: HttpException) : Exception(httpException)

class TokenRequiredException(message: String) : Exception(message)