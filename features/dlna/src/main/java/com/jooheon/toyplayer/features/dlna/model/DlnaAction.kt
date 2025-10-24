package com.jooheon.toyplayer.features.dlna.model

import android.content.Context
import com.jooheon.toyplayer.domain.model.cast.DlnaRendererModel

sealed interface DlnaAction {
    data class OnDiscover(val context: Context): DlnaAction
    data class OnConnect(val context: Context): DlnaAction
    data class OnDisconnect(val context: Context): DlnaAction
    data class OnDlnaSelected(val context: Context, val model: DlnaRendererModel): DlnaAction
}