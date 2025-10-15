package com.jooheon.toyplayer.features.upnp

import android.content.Context
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.features.upnp.protocol.andThen
import com.jooheon.toyplayer.features.upnp.protocol.getCurrentTransportActions
import com.jooheon.toyplayer.features.upnp.protocol.getMute
import com.jooheon.toyplayer.features.upnp.protocol.getProtocolInfos
import com.jooheon.toyplayer.features.upnp.protocol.getVolume
import com.jooheon.toyplayer.features.upnp.protocol.logStep
import com.jooheon.toyplayer.features.upnp.protocol.play
import com.jooheon.toyplayer.features.upnp.protocol.setMute
import com.jooheon.toyplayer.features.upnp.protocol.setPlayMode
import com.jooheon.toyplayer.features.upnp.protocol.setUri
import com.jooheon.toyplayer.features.upnp.protocol.setVolume
import com.jooheon.toyplayer.features.upnp.protocol.stop
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.jupnp.android.AndroidUpnpService
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.support.model.PlayMode
import org.jupnp.support.model.ProtocolInfos
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DlnaPlayerController @Inject constructor(
    @param:ApplicationContext private val context: Context,
    dlnaServiceManager: DlnaServiceManager
) {
    private val parentScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var sessionScope: CoroutineScope? = null

    @Volatile
    internal var renderer: RemoteDevice? = null

    @Volatile
    internal var service: AndroidUpnpService? = null

    private var protocolInfos: ProtocolInfos? = null

    init {
        parentScope.launch {
            dlnaServiceManager.serviceFlow.collect {
                if (it == null) disConnect()
                service = it
            }
        }
    }

    fun connect(renderer: RemoteDevice) = parentScope.launch {
        val service = service ?: return@launch

        disConnect()
        createSessionScope()
        this@DlnaPlayerController.renderer = renderer

        renderer.getProtocolInfos(service)
            .logStep("getProtocolInfos")
            .onSuccess { protocolInfos = it }
    }

    fun disConnect() {
        renderer?.let { stop() }
        sessionScope?.cancel()
        sessionScope = null
        protocolInfos = null
        renderer = null
    }

    fun pause() = sessionScope?.launch {
        val service = service ?: return@launch
        val renderer = renderer ?: return@launch

        renderer.stop(service).logStep("pause")
    }

    fun stop() = sessionScope?.launch {
        val service = service ?: return@launch
        val renderer = renderer ?: return@launch

        renderer.stop(service).logStep("stop")
    }

    fun setPlayMode(playMode: PlayMode) = sessionScope?.launch {
        val service = service ?: return@launch
        val renderer = renderer ?: return@launch

        renderer.setPlayMode(service, playMode).logStep("setPlayMode")
    }

    fun setUri(uri: String) = sessionScope?.launch {
        val service = service ?: return@launch
        val renderer = renderer ?: return@launch

        renderer.stop(service).logStep("stop")
            .andThen { renderer.setPlayMode(service, PlayMode.NORMAL).logStep("setPlayMode") }
            .andThen { renderer.setUri(service, uri.buildUri(context)).logStep("setUri") }
            .andThen { renderer.play(service).logStep("play") }
    }

    fun setMute(mute: Boolean) = sessionScope?.launch {
        val service = service ?: return@launch
        val renderer = renderer ?: return@launch

        renderer.setMute(service, mute).logStep("setMute")
    }

    fun getCurrentTransportActions() = sessionScope?.launch {
        val service = service ?: return@launch
        val renderer = renderer ?: return@launch

        renderer.getCurrentTransportActions(service).logStep("getCurrentTransportActions")
    }

    fun getVolume() = sessionScope?.launch {
        val service = service ?: return@launch
        val renderer = renderer ?: return@launch

        renderer.getVolume(service).logStep("getVolume")
    }

    private fun String.buildUri(context: Context): String {
        val phoneIp = wifiIp(context)
        val mediaUrl = "http://$phoneIp:${HttpProxyServer.PORT}/?u=" + URLEncoder.encode(this, "UTF-8")
        return mediaUrl
    }

    private fun wifiIp(context: Context): String {
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
        val ip = wm.connectionInfo.ipAddress
        val bytes = byteArrayOf((ip and 0xff).toByte(), (ip shr 8 and 0xff).toByte(),
            (ip shr 16 and 0xff).toByte(), (ip shr 24 and 0xff).toByte())
        return java.net.InetAddress.getByAddress(bytes).hostAddress.defaultEmpty()
    }

    private fun createSessionScope() {
        sessionScope?.cancel()
        sessionScope = null

        val job = SupervisorJob(parentScope.coroutineContext[Job])
        val dispatcher = Dispatchers.Main
        sessionScope = CoroutineScope(job + dispatcher)
    }
}