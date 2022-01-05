package com.example.rxtest.domain.common

data class BaseResponse<T>(
    val result: T,
    val detail: String
)