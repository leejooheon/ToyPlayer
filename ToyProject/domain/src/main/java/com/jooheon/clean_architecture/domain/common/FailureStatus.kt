package com.jooheon.clean_architecture.domain.common

enum class FailureStatus {
    EMPTY,
    API_FAIL,
    NO_INTERNET,
    JSON_PARSE,
    OTHER
}