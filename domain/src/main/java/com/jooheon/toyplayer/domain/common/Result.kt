package com.jooheon.toyplayer.domain.common

import com.jooheon.toyplayer.domain.common.errors.EmptyResult
import com.jooheon.toyplayer.domain.common.errors.RootError

sealed interface Result<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): Result<D, E>
    data class Error<out E: RootError>(val error: E): Result<Nothing, E>
}

inline fun <T, E: RootError, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

inline fun <T, E: RootError> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
    }
}
inline fun <T, E: RootError> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> {
            action(error)
            this
        }
        is Result.Success -> this
    }
}

fun <T, E: RootError> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  }
}