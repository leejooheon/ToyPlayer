package com.jooheon.toyplayer.features.dlna

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.system.network.NetworkConnectivityObserver
import com.jooheon.toyplayer.core.system.network.WifiConnectivity
import com.jooheon.toyplayer.domain.castapi.CastStateHolder
import com.jooheon.toyplayer.domain.model.cast.DlnaRendererModel
import com.jooheon.toyplayer.domain.model.common.extension.default
import com.jooheon.toyplayer.features.dlna.model.DlnaAction
import com.jooheon.toyplayer.features.musicservice.data.PlayerType
import com.jooheon.toyplayer.features.musicservice.player.CustomCommand
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.dlna.model.DlnaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DlnaViewModel @Inject constructor(
    private val castStateHolder: CastStateHolder,
    private val playerController: PlayerController,
    private val wifiConnectivity: WifiConnectivity
): ViewModel() {
    private val _uiState = MutableStateFlow(DlnaUiState.default)
    internal val uiState = _uiState.asStateFlow()

    init {
        observeStates()
    }

    internal fun dispatch(action: DlnaAction) = viewModelScope.launch {
        when(action) {
            is DlnaAction.OnDiscover -> onDiscover(action.context)
            is DlnaAction.OnConnect -> onConnect(action.context)
            is DlnaAction.OnDisconnect -> onDisconnect(action.context)
            is DlnaAction.OnDlnaSelected -> onDlnaSelected(action.context, action.model)
        }
    }

    private fun onDiscover(context: Context) {
        playerController.sendCustomCommand(
            context = context,
            command = CustomCommand.DlnaDiscover,
            listener = {
                Timber.d("discover: $it")
            }
        )
    }

    private fun onConnect(context: Context) {
        playerController.sendCustomCommand(
            context = context,
            command = CustomCommand.SelectPlayerType(PlayerType.DLNA),
            listener = {
                Timber.d("onConnect: $it")
            }
        )
    }

    private fun onDisconnect(context: Context) {
        playerController.sendCustomCommand(
            context = context,
            command = CustomCommand.SelectPlayerType(PlayerType.LOCAL),
            listener = {
                Timber.d("onDisconnect: $it")
            }
        )
    }

    private fun onDlnaSelected(context: Context, model: DlnaRendererModel) {
        playerController.sendCustomCommand(
            context = context,
            command = CustomCommand.OnDlnaSelected(model),
            listener = {
                Timber.d("onDlnaSelected: $it")
            }
        )
    }
    private fun observeStates() = viewModelScope.launch {
        launch {
            castStateHolder.state.collectLatest { state ->
                _uiState.update {
                    it.copy(state = state)
                }
            }
        }
        launch {
            castStateHolder.connectionState.collectLatest { connectionState ->
                _uiState.update {
                    it.copy(connectionState = connectionState)
                }
            }
        }
        launch {
            castStateHolder.rendererListModel.collectLatest { rendererList ->
                _uiState.update {
                    it.copy(rendererList = rendererList.toPersistentList())
                }
            }
        }
        launch {
            castStateHolder.selectedRendererModel.collectLatest { selectedRenderer ->
                val renderer = selectedRenderer.default(DlnaRendererModel.default)
                _uiState.update {
                    it.copy(selectedRenderer = renderer)
                }
            }
        }
        launch {
            wifiConnectivity.observe().collectLatest {
                Timber.d("wifiConnectivity: $it")
                val connected = it == NetworkConnectivityObserver.Status.Available
                _uiState.update {
                    it.copy(wifiConnected = connected)
                }
            }
        }

//        launch {
//            playerController.customEvents.collect {
//                Timber.d("customEvents: $it")
//                if(it is CustomCommand.OnDlnaDiscovered) {
//                    Timber.d("test: ${it.models}")
//                    if(it.models.isNotEmpty()) {
//                        _testChannel.send(it.models.first())
//                    }
//                }
//            }
//        }
    }
}