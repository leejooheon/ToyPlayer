package com.jooheon.toyplayer.domain.castapi

import com.jooheon.toyplayer.domain.model.cast.DlnaRendererModel
import com.jooheon.toyplayer.domain.model.cast.DlnaStateModel
import com.jooheon.toyplayer.domain.model.dlna.DlnaConnectionState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface CastStateHolder {
    val state: StateFlow<DlnaStateModel>

    val connectionState: SharedFlow<DlnaConnectionState>

    val rendererListModel: StateFlow<List<DlnaRendererModel>>

    val selectedRendererModel: StateFlow<DlnaRendererModel?>
}