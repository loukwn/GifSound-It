package com.loukwn.gifsoundit.domain

sealed class DataState<out T> {
    /**
     * Returns the Success value or null.
     *
     * Can be invoked as an operator like: `yourProp()`
     */
    open operator fun invoke(): T? = null

    class Loading<out T>(
        private val cachedData: T? = null
    ) : DataState<T>() {
        override fun equals(other: Any?) = other is Loading<*>

        override fun hashCode() = "Loading".hashCode()

        override fun invoke(): T? = cachedData
    }

    data class Data<out T>(private val data: T) : DataState<T>() {
        override operator fun invoke(): T = data
    }

    data class Error<out T>(
        val error: Throwable,
        val formattedError: String?,
        val cachedData: T? = null
    ) : DataState<T>() {
        override fun equals(other: Any?): Boolean {
            if (other !is Error<*>) return false

            val otherError = other.error
            return error::class == otherError::class &&
                formattedError == other.formattedError &&
                error.message == otherError.message &&
                error.stackTrace.firstOrNull() == otherError.stackTrace.firstOrNull()
        }

        override fun hashCode(): Int =
            arrayOf(error::class, formattedError, error.message, error.stackTrace[0]).contentHashCode()

        override fun invoke(): T? = cachedData
    }
}
