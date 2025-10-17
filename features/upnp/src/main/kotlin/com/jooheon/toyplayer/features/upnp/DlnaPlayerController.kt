package com.jooheon.toyplayer.features.upnp

import android.content.Context
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import com.jooheon.toyplayer.domain.castapi.CastController
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.features.upnp.protocol.andThen
import com.jooheon.toyplayer.features.upnp.protocol.getCurrentTransportActions
import com.jooheon.toyplayer.features.upnp.protocol.getDeviceCapabilities
import com.jooheon.toyplayer.features.upnp.protocol.getMediaInfo
import com.jooheon.toyplayer.features.upnp.protocol.getPositionInfo
import com.jooheon.toyplayer.features.upnp.protocol.getProtocolInfos
import com.jooheon.toyplayer.features.upnp.protocol.getTransportInfo
import com.jooheon.toyplayer.features.upnp.protocol.getVolume
import com.jooheon.toyplayer.features.upnp.protocol.logStep
import com.jooheon.toyplayer.features.upnp.protocol.parseDurationToMillis
import com.jooheon.toyplayer.features.upnp.protocol.pause
import com.jooheon.toyplayer.features.upnp.protocol.play
import com.jooheon.toyplayer.features.upnp.protocol.seekTo
import com.jooheon.toyplayer.features.upnp.protocol.setMute
import com.jooheon.toyplayer.features.upnp.protocol.setPlayMode
import com.jooheon.toyplayer.features.upnp.protocol.setUri
import com.jooheon.toyplayer.features.upnp.protocol.stop
import com.jooheon.toyplayer.features.upnp.protocol.supportsAac
import com.jooheon.toyplayer.features.upnp.protocol.supportsFlac
import com.jooheon.toyplayer.features.upnp.protocol.supportsHls
import com.jooheon.toyplayer.features.upnp.protocol.supportsMp3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.support.model.PlayMode
import timber.log.Timber
import java.net.URLEncoder

class DlnaPlayerController(
    private val context: Context,
    private val scope: CoroutineScope,
    private val dlnaStateHolder: DlnaStateHolder,
): CastController {
    private var sessionScope: CoroutineScope? = null

    suspend fun connect(renderer: RemoteDevice) {
        val service = dlnaStateHolder.service.value ?: return

        createSessionScope()
        getTransportInfo()

        renderer.getProtocolInfos(service)
            .logStep("getProtocolInfos")
            .onSuccess {
                Timber.i("supports: [Aac: ${it.supportsAac()}], [Mp3: ${it.supportsMp3()}], [Flac: ${it.supportsFlac()}], [Hls: ${it.supportsHls()}]")
                dlnaStateHolder.onProtocolInfosChanged(it)
            }
    }

    fun disConnect() {
        sessionScope?.cancel()
        sessionScope = null
        dlnaStateHolder.clear()
    }

    override fun play(
        uri: String,
        seekTo: Long,
    ) {
        sessionScope?.launch {
            val service = dlnaStateHolder.service.value ?: return@launch
            val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

            renderer.setUri(service, uri.buildUri(context)).logStep("setUri")
                .andThen { renderer.play(service).logStep("play") }
                .also {
                    if(seekTo == -1L) return@also
                    it.andThen { renderer.seekTo(service, seekTo).logStep("seek") }
                }
        }
    }

    override fun resume() {
        sessionScope?.launch {
            val service = dlnaStateHolder.service.value ?: return@launch
            val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

            renderer.getMediaInfo(service).logStep("getMediaInfo")
                .onSuccess {
                    it.currentURI?.let {
                        renderer.play(service).logStep("play")
                    }
                }
        }
    }

    override fun pause() {
        sessionScope?.launch {
            val service = dlnaStateHolder.service.value ?: return@launch
            val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

            renderer.pause(service).logStep("pause")
        }
    }

    override fun stop(){
        sessionScope?.launch {
            val service = dlnaStateHolder.service.value ?: return@launch
            val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

            renderer.stop(service).logStep("stop")
        }
    }

    override fun seekTo(positionMs: Long) {
        sessionScope?.launch {
            val service = dlnaStateHolder.service.value ?: return@launch
            val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

            renderer.seekTo(service, positionMs).logStep("seekTo")
        }
    }


    override fun shuffleModeEnabled(enabled: Boolean) {
        sessionScope?.launch {
            val service = dlnaStateHolder.service.value ?: return@launch
            val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch
            if(enabled) PlayMode.SHUFFLE else PlayMode.NORMAL

            renderer.setPlayMode(service, PlayMode.SHUFFLE).logStep("setPlayMode")
        }
    }

    override fun repeatMode(mode: Int) {
        sessionScope?.launch {
            val service = dlnaStateHolder.service.value ?: return@launch
            val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

            val playMode = when(mode) {
                REPEAT_MODE_OFF -> PlayMode.NORMAL
                REPEAT_MODE_ONE -> PlayMode.REPEAT_ONE
                REPEAT_MODE_ALL -> PlayMode.REPEAT_ALL
                else -> throw IllegalArgumentException("Unknown repeat mode: $mode")
            }
            
            renderer.setPlayMode(service, playMode).logStep("setPlayMode")
        }
    }

    fun setMute(mute: Boolean) = sessionScope?.launch {
        val service = dlnaStateHolder.service.value ?: return@launch
        val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

        renderer.setMute(service, mute).logStep("setMute")
    }

    fun getCurrentTransportActions() = sessionScope?.launch {
        val service = dlnaStateHolder.service.value ?: return@launch
        val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

        renderer.getCurrentTransportActions(service).logStep("getCurrentTransportActions")
    }

    fun getDeviceCapabilities() = sessionScope?.launch {
        val service = dlnaStateHolder.service.value ?: return@launch
        val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

        renderer.getDeviceCapabilities(service).logStep("getDeviceCapabilities")
    }

    fun getMediaInfo() = sessionScope?.launch {
        val service = dlnaStateHolder.service.value ?: return@launch
        val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

        renderer.getMediaInfo(service).logStep("getMediaInfo")
    }
    fun getPositionInfo() = sessionScope?.launch {
        val service = dlnaStateHolder.service.value ?: return@launch
        val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

        renderer.getPositionInfo(service).logStep("getPositionInfo")
            .onSuccess {
                val position = parseDurationToMillis(it.relTime)
                dlnaStateHolder.onPositionChanged(position)
            }
    }
    fun getTransportInfo() = sessionScope?.launch {
        val service = dlnaStateHolder.service.value ?: return@launch
        val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

        renderer.getTransportInfo(service)
            .logStep("getTransportInfo")
            .onSuccess {
                dlnaStateHolder.onStateChanged(it.currentTransportState.value)
                dlnaStateHolder.onConnectionStateChanged(it.currentTransportStatus.value)
            }
    }

    fun getVolume() = sessionScope?.launch {
        val service = dlnaStateHolder.service.value ?: return@launch
        val renderer = dlnaStateHolder.selectedRenderer.value ?: return@launch

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

        val job = SupervisorJob(scope.coroutineContext[Job])
        val dispatcher = Dispatchers.Main
        sessionScope = CoroutineScope(job + dispatcher)
    }
}