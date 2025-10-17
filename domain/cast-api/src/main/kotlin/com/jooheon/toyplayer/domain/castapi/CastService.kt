package com.jooheon.toyplayer.domain.castapi

import com.jooheon.toyplayer.domain.model.cast.DlnaRendererModel


interface CastService {
    fun bindService()
    fun unbindService()
    fun selectRenderer(renderer: DlnaRendererModel?)
}