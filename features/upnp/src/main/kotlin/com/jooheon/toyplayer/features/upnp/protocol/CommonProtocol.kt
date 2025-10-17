package com.jooheon.toyplayer.features.upnp.protocol

import com.jooheon.toyplayer.domain.model.common.extension.default
import org.jupnp.model.message.UpnpResponse
import org.jupnp.model.types.UnsignedIntegerFourBytes
import org.jupnp.support.avtransport.lastchange.AVTransportLastChangeParser
import org.jupnp.support.avtransport.lastchange.AVTransportVariable
import org.jupnp.support.lastchange.LastChange
import org.jupnp.support.model.DeviceCapabilities
import org.jupnp.support.model.MediaInfo
import org.jupnp.support.model.ProtocolInfos
import org.jupnp.support.model.TransportInfo
import timber.log.Timber

internal val instanceId = 0
internal val fixedInstanceId = UnsignedIntegerFourBytes(instanceId.toLong())

internal fun UpnpResponse?.toMessage(defaultMsg: String?): String =
    this?.let { "status: ${it.statusMessage}, details: ${it.responseDetails}" }
        .default(defaultMsg.default("UpnpResponse is null"))

internal fun ProtocolInfos.supportsAac() = supports("audio/aac", "audio/mp4")
internal fun ProtocolInfos.supportsMp3() = supports("audio/mpeg", "audio/mp3")
internal fun ProtocolInfos.supportsFlac() = supports("audio/flac")
internal fun ProtocolInfos.supportsHls() = supports(
    "application/vnd.apple.mpegurl",
    "application/x-mpegurl",
    "audio/mpegurl"
)

private fun ProtocolInfos.supports(vararg mimeTypes: String): Boolean {
    if (isEmpty()) return false

    return this.any { protocolInfo ->
        val format = protocolInfo.contentFormat.lowercase()
        mimeTypes.any { mime ->
            format.contains(mime.lowercase())
        }
    }
}
suspend inline fun <T, R> Result<T>.andThen(
    crossinline transform: suspend (T) -> Result<R>
): Result<R> = fold(
    onSuccess = { value -> transform(value) },
    onFailure = { e -> Result.failure(e) }
)

inline fun <T> Result<T>.tap(onSuccess: (T) -> Unit, onFailure: (Throwable) -> Unit = {}): Result<T> =
    also { it.onSuccess(onSuccess).onFailure(onFailure) }

internal fun <T> Result<T>.logStep(step: String): Result<T> =
    tap(
        onSuccess = {
            val message = when(it) {
                is MediaInfo -> it.prettyString()
                is TransportInfo -> it.prettyString()
                is DeviceCapabilities -> it.prettyString()
                else -> it.toString()
            }
            Timber.d("$step success: $message")
        },
        onFailure = { Timber.w("$step failure: ${it.message}") }
    )

internal fun TransportInfo.prettyString(): String = buildString {
    append("state=${currentTransportState}, ")
    append("status=${currentTransportStatus}, ")
    append("speed=${currentSpeed}")
    append(")")
}

internal fun MediaInfo.prettyString(): String = buildString {
    append("MediaInfo(")
    append("currentURI=${currentURI}, ")
    append("currentURIMetaData=${currentURIMetaData}, ")
    append("nextURI=${nextURI}, ")
    append("nextURIMetaData=${nextURIMetaData}, ")
    append("numberOfTracks=${numberOfTracks?.value}, ")
    append("mediaDuration=${mediaDuration}, ")
    append("playMedium=${playMedium}, ")
    append("recordMedium=${recordMedium}, ")
    append("writeStatus=${writeStatus}")
    append(")")
}

internal fun DeviceCapabilities.prettyString(): String = buildString {
    append("playMedia=${playMediaString}, ")
    append("recMedia=${recMediaString}, ")
    append("recQualityModes=${recQualityModesString}")
    append(")")
}

internal fun parseAvTransportLastChange(xml: String) {
    try {
        val last = LastChange(AVTransportLastChangeParser(), xml)

        val map = mutableMapOf<String, Any?>()
        val id = instanceId

        map["TransportState"] = last.getEventedValue(id, AVTransportVariable.TransportState::class.java)?.value
        map["TransportStatus"] = last.getEventedValue(id, AVTransportVariable.TransportStatus::class.java)?.value
        map["RecordStorageMedium"] = last.getEventedValue(id, AVTransportVariable.RecordStorageMedium::class.java)?.value
        map["PossibleRecordStorageMedia"] = last.getEventedValue(id, AVTransportVariable.PossibleRecordStorageMedia::class.java)?.value
        map["PossiblePlaybackStorageMedia"] = last.getEventedValue(id, AVTransportVariable.PossiblePlaybackStorageMedia::class.java)?.value
        map["CurrentPlayMode"] = last.getEventedValue(id, AVTransportVariable.CurrentPlayMode::class.java)?.value
        map["TransportPlaySpeed"] = last.getEventedValue(id, AVTransportVariable.TransportPlaySpeed::class.java)?.value
        map["RecordMediumWriteStatus"] = last.getEventedValue(id, AVTransportVariable.RecordMediumWriteStatus::class.java)?.value
        map["CurrentRecordQualityMode"] = last.getEventedValue(id, AVTransportVariable.CurrentRecordQualityMode::class.java)?.value
        map["PossibleRecordQualityModes"] = last.getEventedValue(id, AVTransportVariable.PossibleRecordQualityModes::class.java)?.value
        map["NumberOfTracks"] = last.getEventedValue(id, AVTransportVariable.NumberOfTracks::class.java)?.value
        map["CurrentTrack"] = last.getEventedValue(id, AVTransportVariable.CurrentTrack::class.java)?.value
        map["CurrentTrackDuration"] = last.getEventedValue(id, AVTransportVariable.CurrentTrackDuration::class.java)?.value
        map["CurrentMediaDuration"] = last.getEventedValue(id, AVTransportVariable.CurrentMediaDuration::class.java)?.value
        map["CurrentTrackMetaData"] = last.getEventedValue(id, AVTransportVariable.CurrentTrackMetaData::class.java)?.value
        map["CurrentTrackURI"] = last.getEventedValue(id, AVTransportVariable.CurrentTrackURI::class.java)?.value
        map["AVTransportURI"] = last.getEventedValue(id, AVTransportVariable.AVTransportURI::class.java)?.value
        map["NextAVTransportURI"] = last.getEventedValue(id, AVTransportVariable.NextAVTransportURI::class.java)?.value
        map["AVTransportURIMetaData"] = last.getEventedValue(id, AVTransportVariable.AVTransportURIMetaData::class.java)?.value
        map["NextAVTransportURIMetaData"] = last.getEventedValue(id, AVTransportVariable.NextAVTransportURIMetaData::class.java)?.value
        map["CurrentTransportActions"] = last.getEventedValue(id, AVTransportVariable.CurrentTransportActions::class.java)?.value
        map["RelativeTimePosition"] = last.getEventedValue(id, AVTransportVariable.RelativeTimePosition::class.java)?.value
        map["AbsoluteTimePosition"] = last.getEventedValue(id, AVTransportVariable.AbsoluteTimePosition::class.java)?.value
        map["RelativeCounterPosition"] = last.getEventedValue(id, AVTransportVariable.RelativeCounterPosition::class.java)?.value
        map["AbsoluteCounterPosition"] = last.getEventedValue(id, AVTransportVariable.AbsoluteCounterPosition::class.java)?.value

        Timber.d("LastChange:\n${map.entries.joinToString("\n") { "  ${it.key} = ${it.value}" }}")

    } catch (e: Exception) {
        Timber.e(e, "Failed to parse AVTransport LastChange XML")
    }
}



internal fun parseDurationToMillis(input: String?): Long {
    if (input.isNullOrBlank()) return 0L

    // 구분자 기준 split → 맨 뒤가 초로 취급됨
    val clean = input.trim()
        .replace(",", ".") // 일부 기기는 00:04:27,000 형태
        .replace("-", ":") // 혹시 모를 변형 방어
    val parts = clean.split(":").mapNotNull { it.toDoubleOrNull() }

    if (parts.isEmpty()) return 0L

    // hh:mm:ss, mm:ss, ss 등 처리
    val seconds = when (parts.size) {
        4 -> (parts[0] * 3600) + (parts[1] * 60) + parts[2] + (parts[3] / 100.0) // 프레임 또는 소수점
        3 -> (parts[0] * 3600) + (parts[1] * 60) + parts[2]
        2 -> (parts[0] * 60) + parts[1]
        1 -> parts[0]
        else -> 0.0
    }

    // 밀리초 변환
    return (seconds * 1000).toLong()
}