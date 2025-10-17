package com.jooheon.toyplayer.features.upnp.protocol

import com.jooheon.toyplayer.features.upnp.model.DlnaSpec
import org.jupnp.android.AndroidUpnpService
import org.jupnp.model.action.ActionInvocation
import org.jupnp.model.message.UpnpResponse
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.model.types.UnsignedIntegerFourBytes
import org.jupnp.support.igd.callback.GetStatusInfo
import org.jupnp.support.model.Connection
import org.jupnp.support.renderingcontrol.callback.GetMute
import org.jupnp.support.renderingcontrol.callback.GetVolume
import org.jupnp.support.renderingcontrol.callback.SetMute
import org.jupnp.support.renderingcontrol.callback.SetVolume
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val spec = DlnaSpec.RenderingControl

internal suspend inline fun RemoteDevice.getVolume(
    service: AndroidUpnpService
): Result<Int> = suspendCoroutine { continuation ->
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

    val action = object : GetVolume(fixedInstanceId, serviceType) {
        override fun received(
            actionInvocation: ActionInvocation<*>?,
            currentVolume: Int
        ) {
            continuation.resume(Result.success(currentVolume))
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

internal suspend inline fun RemoteDevice.setVolume(
    service: AndroidUpnpService,
    newVolume: Long,
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

    val action = object : SetVolume(serviceType, newVolume) {
        override fun success(invocation: ActionInvocation<*>?) {
            super.success(invocation)
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

internal suspend inline fun RemoteDevice.getMute(
    service: AndroidUpnpService,
): Result<Boolean> = suspendCoroutine { continuation ->
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

    val action = object : GetMute(fixedInstanceId, serviceType) {
        override fun received(
            actionInvocation: ActionInvocation<*>?,
            currentMute: Boolean
        ) {
            continuation.resume(Result.success(currentMute))
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

internal suspend inline fun RemoteDevice.setMute(
    service: AndroidUpnpService,
    newMute: Boolean,
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

    val action = object : SetMute(serviceType, newMute) {
        override fun success(invocation: ActionInvocation<*>?) {
            super.success(invocation)
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


internal suspend inline fun RemoteDevice.getStatusInfo(
    service: AndroidUpnpService,
): Result<Connection.StatusInfo> = suspendCoroutine { continuation ->
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

    val action = object : GetStatusInfo(serviceType) {
        override fun success(statusInfo: Connection.StatusInfo?) {
            statusInfo?.let { continuation.resume(Result.success(it)) }
                ?: continuation.resume(Result.failure(RuntimeException("StatusInfo is null")))
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

private fun RemoteDevice.checkHasServiceType(): Result<Unit> {
    val serviceType = findService(spec.type)

    return if(serviceType == null) {
        Result.failure(RuntimeException("UnSupported service type"))
    } else {
        Result.success(Unit)
    }
}