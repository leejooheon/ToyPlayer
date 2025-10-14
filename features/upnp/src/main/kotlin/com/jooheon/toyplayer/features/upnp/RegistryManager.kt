package com.jooheon.toyplayer.features.upnp

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.jupnp.android.AndroidUpnpService
import org.jupnp.android.AndroidUpnpServiceImpl
import org.jupnp.model.action.ActionInvocation
import org.jupnp.model.message.UpnpResponse
import org.jupnp.model.meta.Device
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.model.types.UDAServiceType
import org.jupnp.model.types.UnsignedIntegerFourBytes
import org.jupnp.registry.DefaultRegistryListener
import org.jupnp.registry.Registry
import org.jupnp.support.avtransport.callback.Pause
import org.jupnp.support.avtransport.callback.Play
import org.jupnp.support.avtransport.callback.SetAVTransportURI
import org.jupnp.support.avtransport.callback.SetPlayMode
import org.jupnp.support.avtransport.callback.Stop
import org.jupnp.support.connectionmanager.callback.GetProtocolInfo
import org.jupnp.support.model.PlayMode
import org.jupnp.support.model.ProtocolInfos
import timber.log.Timber
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistryManager @Inject constructor() {
    private var upnpService: AndroidUpnpService? = null
    private var renderer: RemoteDevice? = null
    private val proxyServer = HttpProxyServer()

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
                renderer = device

                val serviceType = device.findService(UDAServiceType("ConnectionManager"))
                upnpService?.controlPoint?.execute(object : GetProtocolInfo(serviceType) {
                    override fun received(
                        actionInvocation: ActionInvocation<*>?,
                        sinkProtocolInfos: ProtocolInfos?,
                        sourceProtocolInfos: ProtocolInfos?
                    ) {
                        Timber.d("SINK: $sinkProtocolInfos")
                    }

                    override fun failure(
                        invocation: ActionInvocation<*>?,
                        operation: UpnpResponse?,
                        defaultMsg: String?
                    ) {
                        Timber.d("GetProtocolInfo failure: $defaultMsg")
                    }
                })
            }
        }

        override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice) {
            super.remoteDeviceRemoved(registry, device)
            Timber.d("Device removed: ${device.getDisplayString()}");
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName?,
            service: IBinder?
        ) {
            val service = (service as AndroidUpnpService)
                .also { upnpService = it }
                .also { it.get().startup() }

            service.registry.addListener(registryListener)
            service.controlPoint.search()
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            upnpService = null
            renderer = null
        }
    }

    fun bindService(context: Context) {
        context.applicationContext.bindService(
            Intent(context, AndroidUpnpServiceImpl::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
        proxyServer.start()
    }

    fun unbindService(context: Context) {
        upnpService?.registry?.removeListener(registryListener)
        context.applicationContext.unbindService(serviceConnection)
        proxyServer.stop()
    }

    fun playTest(context: Context) {
        val service = upnpService ?: return
        val renderer = renderer ?: return

        val instanceId = UnsignedIntegerFourBytes(0)
        val avTransport = renderer.findService(UDAServiceType("AVTransport"))

        service.controlPoint.execute(
            object : Stop(instanceId, avTransport) {
                override fun success(invocation: ActionInvocation<*>?) {
                    super.success(invocation)
                    Timber.d("Stop success")
                }
                override fun failure(
                    invocation: ActionInvocation<*>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    Timber.d("Stop failure: $defaultMsg")
                }
            }
        )
        service.controlPoint.execute(
            object : SetPlayMode(instanceId, avTransport, PlayMode.NORMAL) {
                override fun success(invocation: ActionInvocation<*>?) {
                    super.success(invocation)
                    Timber.d("SetPlayMode success")
                }

                override fun failure(
                    invocation: ActionInvocation<*>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    Timber.d("SetPlayMode failure: $defaultMsg")
                }
            }
        )

        val phoneIp = wifiIp(context)
        val mediaUrl = "http://$phoneIp:18080/?u=" + URLEncoder.encode(
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",
            "UTF-8"
        )

        val setUriAction = object : SetAVTransportURI(instanceId, avTransport, mediaUrl, "") {
            override fun success(invocation: ActionInvocation<*>) {
                Timber.d("setUriAction success")
                val playAction = object : Play(instanceId, avTransport, "1") {
                    override fun success(i: ActionInvocation<*>) {
                        Timber.d("Play success")
                    }
                    override fun failure(
                        invocation: ActionInvocation<*>?,
                        operation: UpnpResponse?,
                        defaultMsg: String?
                    ) {
                        Timber.d("Play failure: $defaultMsg")
                    }
                }
                service.controlPoint.execute(playAction)
            }

            override fun failure(invocation: ActionInvocation<*>, op: UpnpResponse?, defaultMsg: String?) {
                Timber.e("SetAVTransportURI failed: $defaultMsg")
            }
        }

        service.controlPoint.execute(setUriAction)
    }

    fun pauseTest() {
        val service = upnpService ?: return
        val renderer = renderer ?: return

        val avTransport = renderer.findService(UDAServiceType("AVTransport"))

        val instanceId = UnsignedIntegerFourBytes(0) // 대부분 0번 인스턴스

        val pauseAction = object : Pause(instanceId, avTransport) {
            override fun success(invocation: ActionInvocation<*>) {
                Timber.d("Pause success")
            }

            override fun failure(
                invocation: ActionInvocation<*>,
                operation: UpnpResponse?,
                defaultMsg: String?
            ) {
                Timber.d("Pause failure: $defaultMsg")
            }
        }

        service.controlPoint.execute(pauseAction)
    }

    private fun wifiIp(context: Context): String {
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
        val ip = wm.connectionInfo.ipAddress
        val bytes = byteArrayOf((ip and 0xff).toByte(), (ip shr 8 and 0xff).toByte(),
            (ip shr 16 and 0xff).toByte(), (ip shr 24 and 0xff).toByte())
        return java.net.InetAddress.getByAddress(bytes).hostAddress
    }

    private fun String.xmlEsc(): String {
        return this
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}