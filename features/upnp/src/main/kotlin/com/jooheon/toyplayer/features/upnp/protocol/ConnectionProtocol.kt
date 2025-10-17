package com.jooheon.toyplayer.features.upnp.protocol

import com.jooheon.toyplayer.features.upnp.model.DlnaSpec
import org.jupnp.android.AndroidUpnpService
import org.jupnp.controlpoint.SubscriptionCallback
import org.jupnp.model.action.ActionInvocation
import org.jupnp.model.gena.CancelReason
import org.jupnp.model.gena.GENASubscription
import org.jupnp.model.message.UpnpResponse
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.support.avtransport.lastchange.AVTransportLastChangeParser
import org.jupnp.support.avtransport.lastchange.AVTransportVariable
import org.jupnp.support.connectionmanager.callback.ConnectionComplete
import org.jupnp.support.connectionmanager.callback.GetCurrentConnectionInfo
import org.jupnp.support.connectionmanager.callback.GetProtocolInfo
import org.jupnp.support.connectionmanager.callback.PrepareForConnection
import org.jupnp.support.lastchange.LastChange
import org.jupnp.support.model.ConnectionInfo
import org.jupnp.support.model.ProtocolInfos
import timber.log.Timber
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val spec = DlnaSpec.ConnectionManager

// 이 장치가 어떤 코덱/컨테이너/프로토콜을 지원하는가?
internal suspend inline fun RemoteDevice.getProtocolInfos(
    service: AndroidUpnpService
): Result<ProtocolInfos> = suspendCoroutine { continuation ->
    checkHasServiceType()
        .onFailure {
            continuation.resume(Result.failure(it))
            return@suspendCoroutine
        }
    
    val controlPoint = service.controlPoint ?: run {
        continuation.resume(Result.failure(RuntimeException("Control point is null")))
        return@suspendCoroutine
    }

    val serviceType = findService(spec.type)

    val action = object : GetProtocolInfo(serviceType, controlPoint) {
        override fun received(
            actionInvocation: ActionInvocation<*>?,
            sinkProtocolInfos: ProtocolInfos?, // 렌더러(DMR)이 재생 가능한 포맷
            sourceProtocolInfos: ProtocolInfos? // 서버(DMS)가 전송 가능한 포맷
        ) {
            if(sinkProtocolInfos == null) {
                continuation.resume(Result.failure(RuntimeException("ProtocolInfos is null")))
                return
            }

            continuation.resume(Result.success(sinkProtocolInfos))
        }

        override fun failure(
            invocation: ActionInvocation<*>,
            operation: UpnpResponse?,
            defaultMsg: String?
        ) {
            continuation.resume(Result.failure(RuntimeException(operation.toMessage(defaultMsg))))
        }
    }

    service.controlPoint.execute(action)
}

internal suspend inline fun RemoteDevice.getCurrentConnectionInfo(
    service: AndroidUpnpService,
    connectionId: Int,
): Result<ConnectionInfo> = suspendCoroutine { continuation ->
    checkHasServiceType()
        .onFailure {
            continuation.resume(Result.failure(it))
            return@suspendCoroutine
        }

    service.controlPoint ?: run {
        continuation.resume(Result.failure(RuntimeException("ControlPoint is null")))
        return@suspendCoroutine
    }
    
    val serviceType = findService(spec.type)

    val action = object : GetCurrentConnectionInfo(serviceType, connectionId) {
        override fun received(
            invocation: ActionInvocation<*>?,
            connectionInfo: ConnectionInfo?
        ) {
            connectionInfo?.let { continuation.resume(Result.success(it)) }
                ?: continuation.resume(Result.failure(RuntimeException("ConnectionInfo is null")))
        }

        override fun failure(
            invocation: ActionInvocation<*>,
            operation: UpnpResponse?,
            defaultMsg: String?
        ) {
            continuation.resume(Result.failure(RuntimeException(operation.toMessage(defaultMsg))))
        }
    }

    service.controlPoint.execute(action)
}

internal suspend inline fun RemoteDevice.connectionComplete(
    service: AndroidUpnpService,
    connectionId: Int,
): Result<ActionInvocation<*>?> = suspendCoroutine { continuation ->
    checkHasServiceType()
        .onFailure {
            continuation.resume(Result.failure(it))
            return@suspendCoroutine
        }

    service.controlPoint ?: run {
        continuation.resume(Result.failure(RuntimeException("ControlPoint is null")))
        return@suspendCoroutine
    }
    
    val serviceType = findService(spec.type)
    val action = object : ConnectionComplete(serviceType, connectionId) {
        override fun success(invocation: ActionInvocation<*>?) {
            continuation.resume(Result.success(invocation))
        }

        override fun failure(
            invocation: ActionInvocation<*>,
            operation: UpnpResponse?,
            defaultMsg: String?
        ) {
            continuation.resume(Result.failure(RuntimeException(operation.toMessage(defaultMsg))))
        }
    }

    service.controlPoint.execute(action)
}

internal suspend inline fun RemoteDevice.prepareForConnection(
    service: AndroidUpnpService,
): Result<ConnectionInfo> = suspendCoroutine { continuation ->
    throw IllegalAccessException("Not implemented")
}

private fun RemoteDevice.checkHasServiceType(): Result<Unit> {
    val serviceType = findService(spec.type)

    return if(serviceType == null) {
        Result.failure(RuntimeException("UnSupported service type"))
    } else {
        Result.success(Unit)
    }
}

private fun handleAvTransportLastChange(xml: String) {
    try {
        val last = LastChange(AVTransportLastChangeParser(), xml)
        // InstanceID 0 기준
        val state = last.getEventedValue(0, AVTransportVariable.TransportState::class.java)?.value
        val uri = last.getEventedValue(0, AVTransportVariable.CurrentTrackURI::class.java)?.value
        val relTime = last.getEventedValue(0, AVTransportVariable.RelativeTimePosition::class.java)?.value

        Timber.d("AVT LastChange: state=$state uri=$uri time=$relTime")
        // UI 업데이트/상태 저장 등
    } catch (e: Exception) {
        Timber.w("Failed to parse LastChange: ${e.message}")
    }
}