package com.jooheon.toyplayer.features.upnp

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jupnp.android.AndroidUpnpService
import org.jupnp.android.AndroidUpnpServiceImpl
import org.jupnp.model.meta.Device
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.model.types.UDAServiceType
import org.jupnp.registry.DefaultRegistryListener
import org.jupnp.registry.Registry
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DlnaServiceManager @Inject constructor() {
    private val proxyServer = HttpProxyServer()

    private val _serviceFlow = MutableStateFlow<AndroidUpnpService?>(null)
    val serviceFlow = _serviceFlow.asStateFlow()

    private val _rendererFlow = MutableStateFlow<List<RemoteDevice>>(emptyList())
    val rendererFlow = _rendererFlow.asStateFlow()

    fun bindService(context: Context) {
        context.applicationContext.bindService(
            Intent(context, AndroidUpnpServiceImpl::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    fun unbindService(context: Context) {
        serviceFlow.value?.registry?.removeListener(registryListener)
        context.applicationContext.unbindService(serviceConnection)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName?,
            service: IBinder?
        ) {
            Timber.d("onServiceConnected")

            val service = (service as AndroidUpnpService)
                .also { it.get().startup() }
                .also { _serviceFlow.tryEmit(it) }

            service.registry.addListener(registryListener)
            service.controlPoint.search()
            proxyServer.start()
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            Timber.d("onServiceDisconnected")

            proxyServer.stop()
            _serviceFlow.tryEmit(null)
            _rendererFlow.tryEmit(emptyList())
        }
    }

    private val registryListener = object : DefaultRegistryListener() {
        override fun deviceAdded(registry: Registry?, device: Device<*, *, *>?) {
            super.deviceAdded(registry, device)
            Timber.d("Device added: ${device?.displayString}")
        }
        override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice) {
            super.remoteDeviceAdded(registry, device)
            Timber.d("Found device: ${device.getDetails().getFriendlyName()}")

            if (device.findService(UDAServiceType("AVTransport")) != null) {
                Timber.d("Found DMR: ${device.details?.friendlyName}")
                _rendererFlow.tryEmit(rendererFlow.value + device)
            }
        }

        override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice) {
            super.remoteDeviceRemoved(registry, device)
            Timber.d("Device removed: ${device.getDisplayString()}");
        }
    }
}