package com.jooheon.toyplayer.features.dlna.model

import com.jooheon.toyplayer.domain.model.cast.DlnaRendererModel
import com.jooheon.toyplayer.domain.model.cast.DlnaStateModel
import com.jooheon.toyplayer.domain.model.dlna.DlnaConnectionState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DlnaUiState(
    val state: DlnaStateModel,
    val connectionState: DlnaConnectionState,
    val selectedRenderer: DlnaRendererModel,
    val rendererList: ImmutableList<DlnaRendererModel>,
    val wifiConnected: Boolean,
) {
    companion object {
        val default = DlnaUiState(
            state = DlnaStateModel.default,
            connectionState = DlnaConnectionState.Idle,
            selectedRenderer = DlnaRendererModel.default,
            rendererList = persistentListOf(DlnaRendererModel.default),
            wifiConnected = false,
        )
    }
}