package com.jooheon.toyplayer.features.upnp.protocol

import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.features.upnp.model.DlnaSpec
import org.jupnp.android.AndroidUpnpService
import org.jupnp.model.action.ActionInvocation
import org.jupnp.model.message.UpnpResponse
import org.jupnp.model.meta.RemoteDevice
import org.jupnp.support.avtransport.callback.GetCurrentTransportActions
import org.jupnp.support.avtransport.callback.GetDeviceCapabilities
import org.jupnp.support.avtransport.callback.GetMediaInfo
import org.jupnp.support.avtransport.callback.GetPositionInfo
import org.jupnp.support.avtransport.callback.GetTransportInfo
import org.jupnp.support.avtransport.callback.Next
import org.jupnp.support.avtransport.callback.Pause
import org.jupnp.support.avtransport.callback.Play
import org.jupnp.support.avtransport.callback.Previous
import org.jupnp.support.avtransport.callback.Seek
import org.jupnp.support.avtransport.callback.SetAVTransportURI
import org.jupnp.support.avtransport.callback.SetPlayMode
import org.jupnp.support.avtransport.callback.Stop
import org.jupnp.support.model.DeviceCapabilities
import org.jupnp.support.model.MediaInfo
import org.jupnp.support.model.PlayMode
import org.jupnp.support.model.PositionInfo
import org.jupnp.support.model.TransportAction
import org.jupnp.support.model.TransportInfo
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val spec = DlnaSpec.AVTransport

// 최근 보낸 명령
internal suspend inline fun RemoteDevice.getCurrentTransportActions(
    service: AndroidUpnpService
): Result<List<TransportAction>> = suspendCoroutine { continuation ->
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

    val action = object : GetCurrentTransportActions(fixedInstanceId, serviceType) {
        override fun received(
            actionInvocation: ActionInvocation<*>?,
            actions: Array<out TransportAction?>?
        ) {
            val list = actions?.mapNotNull { it }.defaultEmpty()
            continuation.resume( Result.success(list))
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

internal suspend inline fun RemoteDevice.getDeviceCapabilities(
    service: AndroidUpnpService
): Result<DeviceCapabilities> = suspendCoroutine { continuation ->
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

    val action = object : GetDeviceCapabilities(fixedInstanceId, serviceType) {
        override fun received(
            actionInvocation: ActionInvocation<*>?,
            caps: DeviceCapabilities?
        ) {
            caps?.let { continuation.resume(Result.success(it)) }
                ?: continuation.resume(Result.failure(RuntimeException("DeviceCapabilities is null")))
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

// 현재 큐/미디어의 메타 정보, 총 길이 등.
internal suspend inline fun RemoteDevice.getMediaInfo(
    service: AndroidUpnpService
): Result<MediaInfo> = suspendCoroutine { continuation ->
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

    val action = object : GetMediaInfo(fixedInstanceId, serviceType) {
        override fun received(
            invocation: ActionInvocation<*>?,
            mediaInfo: MediaInfo?
        ) {
            mediaInfo?.nextURI
            mediaInfo?.let { continuation.resume(Result.success(it)) }
                ?: continuation.resume(Result.failure(RuntimeException("MediaInfo is null")))
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

internal suspend inline fun RemoteDevice.getPositionInfo(
    service: AndroidUpnpService
): Result<PositionInfo> = suspendCoroutine { continuation ->
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

    val action = object : GetPositionInfo(fixedInstanceId, serviceType) {
        override fun received(
            invocation: ActionInvocation<*>?,
            positionInfo: PositionInfo?
        ) {
            positionInfo?.let { continuation.resume(Result.success(it)) }
                ?: continuation.resume(Result.failure(RuntimeException("PositionInfo is null")))
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


internal suspend inline fun RemoteDevice.getTransportInfo(
    service: AndroidUpnpService
): Result<TransportInfo> = suspendCoroutine { continuation ->
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

    val action = object : GetTransportInfo(fixedInstanceId, serviceType) {
        override fun received(
            invocation: ActionInvocation<*>?,
            transportInfo: TransportInfo?
        ) {
            transportInfo?.let { continuation.resume(Result.success(it)) }
                ?: continuation.resume(Result.failure(RuntimeException("TransportInfo is null")))
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


internal suspend inline fun RemoteDevice.next(
    service: AndroidUpnpService
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

    val action = object : Next(fixedInstanceId, serviceType) {
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

internal suspend inline fun RemoteDevice.previous(
    service: AndroidUpnpService
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

    val action = object : Previous(fixedInstanceId, serviceType) {
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


internal suspend inline fun RemoteDevice.pause(
    service: AndroidUpnpService
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

    val action = object : Pause(fixedInstanceId, serviceType) {
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


internal suspend inline fun RemoteDevice.stop(
    service: AndroidUpnpService
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

    val action = object : Stop(fixedInstanceId, serviceType) {
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


internal suspend inline fun RemoteDevice.play(
    service: AndroidUpnpService,
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

    val action = object : Play(fixedInstanceId, serviceType) {
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

internal suspend inline fun RemoteDevice.seekTo(
    service: AndroidUpnpService,
    positionMs: Long,
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

    fun msToRelTime(ms: Long): String {
        val totalSec = ms / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    val serviceType = findService(spec.type)
    val realTimeTarget = msToRelTime(positionMs)

    val action = object : Seek(serviceType, realTimeTarget) {
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

internal suspend inline fun RemoteDevice.setUri(
    service: AndroidUpnpService,
    url: String,
): Result<ActionInvocation<*>> = suspendCoroutine { continuation ->
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
    val metadata = ""

    val action = object : SetAVTransportURI(fixedInstanceId, serviceType, url, metadata) {
        override fun success(invocation: ActionInvocation<*>) {
            val result = Result.success(invocation)
            continuation.resume(result)
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



internal suspend inline fun RemoteDevice.setPlayMode(
    service: AndroidUpnpService,
    playMode: PlayMode,
): Result<ActionInvocation<*>> = suspendCoroutine { continuation ->
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

    val action = object : SetPlayMode(fixedInstanceId, serviceType, playMode) {
        override fun success(invocation: ActionInvocation<*>) {
            val result = Result.success(invocation)
            continuation.resume(result)
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