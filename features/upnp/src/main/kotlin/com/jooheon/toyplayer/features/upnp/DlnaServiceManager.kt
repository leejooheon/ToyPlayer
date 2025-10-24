package com.jooheon.toyplayer.features.upnp

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.jooheon.toyplayer.core.system.network.WifiConnectivity
import com.jooheon.toyplayer.domain.castapi.CastService
import com.jooheon.toyplayer.domain.model.cast.DlnaRendererModel
import com.jooheon.toyplayer.domain.model.dlna.DlnaConnectionState
import com.jooheon.toyplayer.features.upnp.model.DlnaSpec
import com.jooheon.toyplayer.features.upnp.model.GenaEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jupnp.android.AndroidUpnpService
import org.jupnp.android.AndroidUpnpServiceImpl
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.registry.DefaultRegistryListener
import org.jupnp.registry.Registry
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

class DlnaServiceManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val dlnaStateHolder: DlnaStateHolder,
    private val dlnaPlayerController: DlnaPlayerController,
    private val wifiConnectivity: WifiConnectivity,
): CastService {
    private val proxyServer = HttpProxyServer()
    private val dlnaConnectionSupervisor = DlnaConnectionSupervisor()

    init {
        scope.launch {
            launch {
                dlnaStateHolder.service.collect {
                    Timber.d("dlnaStateHolder: $it")
                    runCatching {
                        if(it == null) {
                            proxyServer.stop()
                            dlnaPlayerController.disConnect() // 서비스가 null 이면 연결 해제
                            dlnaStateHolder.onConnectionStateChanged(DlnaConnectionState.Idle)
                        } else {
                            proxyServer.start()
                            dlnaStateHolder.onConnectionStateChanged(DlnaConnectionState.ServiceBound)
                        }
                    }
                }
            }

            launch {
                dlnaStateHolder.selectedRenderer.collect {
                    Timber.d("selectedRenderer: $it")
                    if(it == null) dlnaPlayerController.disConnect() // renderer가 null 이면 연결 해제
                    else dlnaPlayerController.connect(it)
                }
            }

            dlnaStateHolder.state
                .map { it.isPlaying }
                .distinctUntilChanged()
                .onEach {
                    while (currentCoroutineContext().isActive && it) {
                        dlnaPlayerController.getPositionInfo()
                        delay(1.seconds)
                    }
                }.launchIn(this)

            dlnaStateHolder.connectionState.flatMapLatest {
                //merge(observeRendererState(), observeWifiConnectivity())
                if(it == DlnaConnectionState.Connected) observeRendererState()
                else emptyFlow<Unit>()
            }.launchIn(this)
        }
    }

    override fun bindService() {
        context.applicationContext.bindService(
            Intent(context, AndroidUpnpServiceImpl::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun unbindService() {
        runCatching {
            dlnaPlayerController.stop()
            context.applicationContext.unbindService(serviceConnection)
        }
    }

    override fun discover() {
        val service = dlnaStateHolder.service.value ?: return
        service.controlPoint.search()
        dlnaStateHolder.onConnectionStateChanged(DlnaConnectionState.Discovering)
    }

    override fun selectRenderer(renderer: DlnaRendererModel) {
        scope.launch {
            val list = dlnaStateHolder.rendererList.value
            val renderer = list.firstOrNull { it.identity.udn.identifierString == renderer.udn }
            dlnaStateHolder.onRendererSelected(renderer)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName?,
            service: IBinder?
        ) {
            Timber.d("onServiceConnected")

            val service = (service as AndroidUpnpService)
                .also { it.get().startup() }
                .apply { registry.addListener(registryListener) }

            dlnaStateHolder.onServiceChanged(service)
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            Timber.d("onServiceDisconnected")

            dlnaStateHolder.service.value?.let {
                it.registry?.removeListener(registryListener)
            }
            dlnaStateHolder.onServiceChanged(null)
            dlnaStateHolder.onRendererListChanged(emptyList())
        }
    }

    private val registryListener = object : DefaultRegistryListener() {
        override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice) {
            super.remoteDeviceAdded(registry, device)
            val hasService = device.findService(DlnaSpec.AVTransport.type) != null
            Timber.i("remoteDeviceAdded[$hasService]: ${device.details?.friendlyName}")

            if (device.findService(DlnaSpec.AVTransport.type) != null) {
                dlnaStateHolder.onRendererListChanged(
                    dlnaStateHolder.rendererList.value + device
                )
            }
        }

        override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice) {
            super.remoteDeviceRemoved(registry, device)
            Timber.i("remoteDeviceRemoved: ${device.getDisplayString()}");
        }
    }

    private fun observeRendererState(): Flow<GenaEvent> = flow {
        val service = dlnaStateHolder.service.value ?: return@flow
        val renderer = dlnaStateHolder.selectedRenderer.value ?: return@flow

        dlnaConnectionSupervisor.observe(service, renderer).collect {
            Timber.d("GenaEvent: $it")
            when(it) {
                is GenaEvent.OnStateChanged -> dlnaStateHolder.onStateChanged(it.state)
                is GenaEvent.OnStatusChanged -> dlnaStateHolder.onConnectionStateChanged(it.status)
                is GenaEvent.OnTrackDurationChanged -> dlnaStateHolder.onTrackDurationChanged(it.duration)
                is GenaEvent.OnPlayModeChanged -> dlnaStateHolder.onPlayModeChanged(it.playMode)
                is GenaEvent.OnEventMissed -> dlnaPlayerController.getTransportInfo()
                is GenaEvent.OnSubscriptionFailed,
                is GenaEvent.OnSubscriptionExpired -> {
                    dlnaStateHolder.onConnectionStateChanged(DlnaConnectionState.Idle)
                    Timber.w("GENA subscription expired, will retry automatically")
                    throw RuntimeException("Subscription expired")
                }
            }
        }
    }

//    private fun observeWifiConnectivity(): Flow<NetworkConnectivityObserver.Status> = flow {
//        wifiConnectivity.observe().collect { status ->
//            Timber.d("Wi-Fi status: $status")
//
//            when (status) {
//                NetworkConnectivityObserver.Status.Lost,
//                NetworkConnectivityObserver.Status.Unavailable -> {
//                    dlnaPlayerController.disConnect() // Wi-Fi 끊기면 연결 해제
//                }
//                else -> Unit
//            }
//
//            emit(status)
//        }
//    }
}