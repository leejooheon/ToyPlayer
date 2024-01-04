package com.jooheon.toyplayer.features.common.compose.data

import com.jooheon.toyplayer.features.essential.base.UiText
import java.util.UUID

data class AlertDialogResource(
    val content: UiText,
    val id: UUID = UUID.randomUUID()
)