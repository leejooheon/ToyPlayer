package com.jooheon.toyplayer.domain.common

data class BaseResponse<T>(
    val result: T,
    val detail: String
)