package com.jooheon.toyplayer.features.musicservice.usecase

import com.jooheon.toyplayer.domain.castapi.CastService
import com.jooheon.toyplayer.domain.castapi.CastStateHolder
import com.jooheon.toyplayer.domain.model.cast.DlnaRendererModel
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.data.RendererType
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
    private val _rendererType = MutableStateFlow(RendererType.LOCAL)
    internal val rendererType = _rendererType.asStateFlow()

    private val _rendererList = MutableStateFlow<List<DlnaRendererModel>>(emptyList())
    internal val rendererList = _rendererList.asStateFlow()

    internal fun bindService() {
        castService.bindService()
    }

    internal fun unbindService() {
        castService.unbindService()
    }

    internal fun selectRenderer(model: DlnaRendererModel?) {
        castService.selectRenderer(model)
    }

    internal fun observeState(scope: CoroutineScope) {
        scope.launch {
            launch {
                castStateHolder.connectionState.collect {

                }
            }
            launch {
                combine(
                    castStateHolder.connectionState,
                    castStateHolder.state,
                ) { connected, state -> connected to state }.collectLatest { (connected, state) ->
                    musicStateHolder.onPlaybackStateChanged(state.toPlayerState(connected))
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
            launch {
                castStateHolder.selectedRendererModel.collect {
                    if(it == null) unbindService()

                    val type = if(it == null) {
                        RendererType.LOCAL
                    } else {
                        RendererType.DLNA
                    }

                    _rendererType.emit(type)
                }
            }
        }
    }
}