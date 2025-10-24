package com.jooheon.toyplayer.features.upnp

import com.jooheon.toyplayer.domain.castapi.CastStateHolder
import com.jooheon.toyplayer.domain.model.cast.DlnaRendererModel
import com.jooheon.toyplayer.domain.model.cast.DlnaStateModel
import com.jooheon.toyplayer.domain.model.dlna.DlnaConnectionState
import com.jooheon.toyplayer.features.upnp.model.toModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jupnp.android.AndroidUpnpService
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.support.model.ProtocolInfos

class DlnaStateHolder(): CastStateHolder {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _service = MutableStateFlow<AndroidUpnpService?>(null)
    internal val service = _service.asStateFlow()

    private val _protocolInfos = MutableStateFlow(ProtocolInfos())
    internal val protocolInfos = _protocolInfos.asStateFlow()

    private val _state = MutableStateFlow(DlnaStateModel.default)
    override val state = _state.asStateFlow()

    private val _connectionState = MutableSharedFlow<DlnaConnectionState>()
    override val connectionState = _connectionState.asSharedFlow()

    private val _rendererList = MutableStateFlow<List<RemoteDevice>>(emptyList())
    internal val rendererList = _rendererList.asStateFlow()

    private val _rendererListModel = MutableStateFlow<List<DlnaRendererModel>>(emptyList())
    override val rendererListModel = _rendererListModel.asStateFlow()

    private val _selectedRenderer = MutableStateFlow<RemoteDevice?>(null)
    internal val selectedRenderer = _selectedRenderer.asStateFlow()

    private val _selectedRendererModel = MutableStateFlow<DlnaRendererModel?>(null)
    override val selectedRendererModel = _selectedRendererModel.asStateFlow()

    init {
        scope.launch {
            launch {
                _selectedRenderer.collectLatest {
                    _selectedRendererModel.emit(it?.toModel())
                }
            }
            launch {
                _rendererList.collectLatest {
                    _rendererListModel.emit(it.map { device -> device.toModel() } )
                }
            }
        }
    }

    internal fun onServiceChanged(service: AndroidUpnpService?) {
        _service.tryEmit(service)
    }

    internal fun onRendererListChanged(devices: List<RemoteDevice>) {
        _rendererList.tryEmit(devices)
    }

    internal fun onRendererSelected(device: RemoteDevice?) {
        clear()
        _selectedRenderer.tryEmit(device)
    }

    internal fun onProtocolInfosChanged(infos: ProtocolInfos) {
        _protocolInfos.tryEmit(infos)
    }

    internal fun onStateChanged(state: String) {
        _state.update {
            it.copy(state = state)
        }
    }

    internal fun onConnectionStateChanged(raw: String) {
        if(raw == "OK") {
            _connectionState.tryEmit(DlnaConnectionState.Connected)
        }
    }
    internal fun onConnectionStateChanged(state: DlnaConnectionState) {
        _connectionState.tryEmit(state)
    }

    internal fun onTrackDurationChanged(duration: Long) {
        _state.update {
            it.copy(duration = duration)
        }
    }

    internal fun onPositionChanged(position: Long) {
        _state.update {
            it.copy(position = position)
        }
    }

    internal fun onPlayModeChanged(playMode: String) {
        // 플레이 모드 변경 처리 (필요시 구현)
    }

    internal fun clear() {
        _state.tryEmit(DlnaStateModel.default)
        _protocolInfos.tryEmit(ProtocolInfos())
        _selectedRenderer.tryEmit(null)

        // rendererList, service는 manager에서 관리하므로 초기화하지 않음
    }
}