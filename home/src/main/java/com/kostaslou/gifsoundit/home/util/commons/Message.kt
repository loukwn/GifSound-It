package com.kostaslou.gifsoundit.home.util.commons

// class that informs the view of what is happening in the business logic

sealed class Message {
    data class Info(val data: MessageCodes) : Message()
    data class Error(val e: Throwable) : Message()

    companion object {
        fun info(data: MessageCodes): Message = Info(data)
        fun error(e: Throwable): Message = Error(e)
    }
}

enum class MessageCodes {
    TOKEN_READY, RECREATED
}
