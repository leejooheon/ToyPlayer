package com.jooheon.clean_architecture.features.common.compose.data

import com.jooheon.clean_architecture.features.essential.base.UiText
import java.util.UUID

data class AlertDialogResource(
    val content: UiText,
    val id: UUID = UUID.randomUUID()
)