package com.jooheon.clean_architecture.common

data class ErrorResponse(
    val detail: String,
    val instance: Any,
    val status: Int,
    val title: String,
    val type: String
)