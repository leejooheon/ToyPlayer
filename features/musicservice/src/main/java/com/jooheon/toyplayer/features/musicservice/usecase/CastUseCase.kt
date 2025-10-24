package com.jooheon.toyplayer.features.musicservice.usecase

import com.jooheon.toyplayer.domain.castapi.CastService
import com.jooheon.toyplayer.domain.castapi.CastStateHolder
import com.jooheon.toyplayer.domain.model.cast.DlnaRendererModel
import com.jooheon.toyplayer.domain.model.dlna.DlnaConnectionState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CastUseCase(
    private val castService: CastService,
    private val castStateHolder: CastStateHolder,
    private val musicStateHolder: MusicStateHolder,
) {
    private val _rendererList = MutableStateFlow<List<DlnaRendererModel>>(emptyList())
    internal val rendererList = _rendererList.asStateFlow()

    internal fun discover() {
        castService.discover()
    }

    internal fun bindService() {
        castService.bindService()
    }

    internal fun unbindService() {
        castService.unbindService()
    }

    internal fun selectRenderer(model: DlnaRendererModel) {
        castService.selectRenderer(model)
    }

    internal fun observeState(scope: CoroutineScope) {
        scope.launch {
            launch {
                combine(
                    castStateHolder.connectionState,
                    castStateHolder.state,
                ) { connected, state -> connected to state }.collectLatest { (connected, state) ->
                    val ready = connected == DlnaConnectionState.Connected
                    musicStateHolder.onPlaybackStateChanged(state.toPlayerState(ready))
                }
            }
            launch {
                castStateHolder.state.collect {
                    musicStateHolder.onIsPlayingChanged(it.isPlaying)
                    musicStateHolder.onCurrentDurationChanged(it.position)
//                    musicStateHolder.onMediaItemChanged() // 메타를 받아서 처리해야함
                }
            }
            launch {
                castStateHolder.rendererListModel.collect {
                    _rendererList.emit(it)
                }
            }
        }
    }
}