package com.novacodestudios.mint.util

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: Exception) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun error(exception: Exception): Resource<Nothing> = Error(exception)
        fun loading(): Resource<Nothing> = Loading
    }
}

inline fun <T> runSafely(block: () -> T): Resource<T> {
    return try {
        Resource.success(block())
    } catch (e: Exception) {
        Resource.error(e)
    }
}