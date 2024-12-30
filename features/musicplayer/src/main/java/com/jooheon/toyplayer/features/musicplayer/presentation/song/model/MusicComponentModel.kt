package com.jooheon.toyplayer.features.musicplayer.presentation.song.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.core.strings.UiText

data class MusicComponentModel(
    val title: UiText,
    val iconImageVector: ImageVector,
    val type: MusicListType,
)