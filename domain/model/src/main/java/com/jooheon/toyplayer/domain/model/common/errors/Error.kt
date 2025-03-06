package com.jooheon.toyplayer.domain.model.common.errors

import com.jooheon.toyplayer.domain.model.common.Result
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable
sealed interface Error {
    fun serialize() = Json.encodeToString(serializer(), this)

    companion object {
        fun String.toErrorOrNull(): Error? = try {
            Json.decodeFromString(serializer(), this)
        } catch (e: SerializationException) {
            null
        }
    }
}

typealias RootError = Error
typealias EmptyResult<E> = Result<Unit, E>