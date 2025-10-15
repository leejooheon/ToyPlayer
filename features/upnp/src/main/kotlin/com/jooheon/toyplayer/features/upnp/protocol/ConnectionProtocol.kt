package com.jooheon.toyplayer.features.upnp.protocol

import com.jooheon.toyplayer.features.upnp.DlnaSpec
import org.jupnp.android.AndroidUpnpService
import org.jupnp.model.action.ActionInvocation
import org.jupnp.model.message.UpnpResponse
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.support.connectionmanager.callback.ConnectionComplete
import org.jupnp.support.connectionmanager.callback.GetCurrentConnectionInfo
import org.jupnp.support.connectionmanager.callback.GetProtocolInfo
import org.jupnp.support.connectionmanager.callback.PrepareForConnection
import org.jupnp.support.model.ConnectionInfo
import org.jupnp.support.model.ProtocolInfos
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val spec = DlnaSpec.ConnectionManager

/**
 *
 */
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
            sinkProtocolInfos: ProtocolInfos?,
            sourceProtocolInfos: ProtocolInfos?
        ) {
            if(sinkProtocolInfos == null || sourceProtocolInfos == null) {
                continuation.resume(Result.failure(RuntimeException("ProtocolInfos is null")))
                return
            }

            val intersection = ProtocolInfos().apply {
                addAll(sinkProtocolInfos)
                retainAll(sourceProtocolInfos)
            }

            continuation.resume(Result.success(intersection))
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