package com.jooheon.toyplayer.core.system.audio

import android.bluetooth.BluetoothA2dp
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.audio.AudioOutputDevice
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import android.provider.Settings
import kotlin.math.roundToInt

internal class AudioOutputObserverImpl(
    private val context: Context
) : AudioOutputObserver {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun observeCurrentOutput(): Flow<AudioOutputDevice> = callbackFlow {
        val receiver = makeReceiver { trySend(getCurrentOutput()) }
        registerReceiver(receiver)

        trySend(getCurrentOutput())
        awaitClose { context.unregisterReceiver(receiver) }
    }.distinctUntilChanged()

    override fun observeAvailableOutputs(): Flow<List<AudioOutputDevice>> = callbackFlow {
        val receiver = makeReceiver { trySend(getAvailableOutputs()) }
        registerReceiver(receiver)

        trySend(getAvailableOutputs())
        awaitClose { context.unregisterReceiver(receiver) }
    }.distinctUntilChanged()

    override fun observeSystemVolume(): Flow<Pair<Int, Int>> = callbackFlow {
        fun send() {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            trySend(currentVolume to maxVolume)
        }

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                send()
            }
        }

        send()

        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            observer
        )

        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }.distinctUntilChanged()

    override fun setVolume(volume: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI)
    }

    private fun getAvailableOutputs(): List<AudioOutputDevice> {
        return audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            .filter { it.isSink }
            .map { it.toOutputType() }
    }

    private fun getCurrentOutput(): AudioOutputDevice {
        val active = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            .firstOrNull { it.isSink }
        return active.toOutputType()
    }

    private fun registerReceiver(receiver: BroadcastReceiver) {
        val filter = IntentFilter().apply {
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)
        }
        context.registerReceiver(receiver, filter)
    }

    private fun makeReceiver(onChange: () -> Unit): BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                onChange()
            }
        }

    private fun AudioDeviceInfo?.toOutputType(): AudioOutputDevice {
        if(this == null) return defaultAudioOutputDevice(context)

        val type = when (type) {
            AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> AudioOutputDevice.Type.Speaker // FIXME
            AudioDeviceInfo.TYPE_TELEPHONY -> AudioOutputDevice.Type.Speaker // FIXME

            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> AudioOutputDevice.Type.Speaker
            AudioDeviceInfo.TYPE_WIRED_HEADSET,
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> AudioOutputDevice.Type.WiredHeadset
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
            AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> AudioOutputDevice.Type.Bluetooth
            AudioDeviceInfo.TYPE_USB_DEVICE,
            AudioDeviceInfo.TYPE_USB_HEADSET -> AudioOutputDevice.Type.Usb
            AudioDeviceInfo.TYPE_HDMI -> AudioOutputDevice.Type.Hdmi
            else -> AudioOutputDevice.Type.Unknown
        }

        val name = productName?.toString() ?: UiText.StringResource(Strings.audio_output_unknown).asString(context)
        return AudioOutputDevice(type, name)
    }

    private fun defaultAudioOutputDevice(context: Context) = AudioOutputDevice(
        type = AudioOutputDevice.Type.Unknown,
        name = UiText.StringResource(Strings.audio_output_unknown).asString(context)
    )
}
