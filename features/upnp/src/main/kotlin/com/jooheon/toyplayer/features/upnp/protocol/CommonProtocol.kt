package com.jooheon.toyplayer.features.upnp.protocol

import com.jooheon.toyplayer.domain.model.common.extension.default
import org.jupnp.model.message.UpnpResponse
import org.jupnp.model.types.UnsignedIntegerFourBytes
import timber.log.Timber

internal val fixedInstanceId = UnsignedIntegerFourBytes(0)

internal fun UpnpResponse?.toMessage(defaultMsg: String?): String =
    this?.let { "status: ${this.statusMessage}, details: ${this.responseDetails}" }
        .default(defaultMsg.default("UpnpResponse is null"))

suspend inline fun <T, R> Result<T>.andThen(
    crossinline transform: suspend (T) -> Result<R>
): Result<R> = fold(
    onSuccess = { value -> transform(value) },
    onFailure = { e -> Result.failure(e) }
)

inline fun <T> Result<T>.tap(onSuccess: (T) -> Unit, onFailure: (Throwable) -> Unit = {}): Result<T> =
    also { it.onSuccess(onSuccess).onFailure(onFailure) }

fun <T> Result<T>.logStep(step: String): Result<T> =
    tap(
        onSuccess = { Timber.d("$step success: $it") },
        onFailure = { Timber.w("$step failure: ${it.message}") }
    )