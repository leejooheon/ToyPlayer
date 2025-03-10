package com.jooheon.toyplayer.features.musicplayer.presentation.song.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.MusicListType

data class MusicComponentModel(
    val title: UiText,
    val iconImageVector: ImageVector,
    val type: MusicListType,
)