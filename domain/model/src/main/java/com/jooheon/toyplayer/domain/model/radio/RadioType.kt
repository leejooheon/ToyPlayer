package com.jooheon.toyplayer.domain.model.radio

import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
sealed class RadioType {
    @Serializable data object Kbs: RadioType()
    @Serializable data object Sbs: RadioType()
    @Serializable data object Mbc: RadioType()
    @Serializable data class Etc(val name: String): RadioType()

    fun name() = if(this is Etc) name else this::class.simpleName.defaultEmpty()
    fun serialize() = json.encodeToString(RadioType.serializer(), this)

    companion object {
        private val radioTypeModule = SerializersModule {
            polymorphic(RadioType::class) {
                subclass(Kbs::class)
                subclass(Sbs::class)
                subclass(Mbc::class)
                subclass(Etc::class)
            }
        }
        private val json = Json {
            serializersModule = radioTypeModule
            ignoreUnknownKeys = true
        }
        fun String.toRadioTypeOrNull(): RadioType? {
            return try {
                json.decodeFromString(RadioType.serializer(), this@toRadioTypeOrNull)
            } catch (e: SerializationException) {
                null
            }
        }
    }
}