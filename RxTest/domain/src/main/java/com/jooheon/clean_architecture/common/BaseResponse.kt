package com.jooheon.clean_architecture.common

data class BaseResponse<T>(
    val result: T,
    val detail: String
)