package com.jooheon.toyplayer.domain.common

enum class FailureStatus {
    EMPTY,
    API_FAIL,
    NO_INTERNET,
    TIMEOUT,
    JSON_PARSE,
    OTHER
}