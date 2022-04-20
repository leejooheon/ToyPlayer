package com.jooheon.clean_architecture.presentation.common

import com.jooheon.clean_architecture.presentation.utils.UiText
import java.util.*

data class AlertDialogResource(
    val content: UiText,
    val id: UUID = UUID.randomUUID()

)