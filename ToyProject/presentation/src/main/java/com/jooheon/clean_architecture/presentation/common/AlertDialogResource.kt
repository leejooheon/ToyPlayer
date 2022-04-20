package com.jooheon.clean_architecture.presentation.common

import java.util.*

data class AlertDialogResource(
    val content: String,
    val id: UUID = UUID.randomUUID()

)