package com.jooheon.clean_architecture.domain.common

data class BaseResponse<T>(
    val result: T,
    val detail: String
)