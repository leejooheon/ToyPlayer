package com.jooheon.toyplayer.features.musicservice.usecase

import com.jooheon.toyplayer.features.musicservice.data.PlayerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerTypeUseCase {
    private val _playerType = MutableStateFlow(PlayerType.LOCAL)
    internal val playerType = _playerType.asStateFlow()

    internal suspend fun setPlayerType(type: PlayerType) {
        _playerType.emit(type)
    }
}