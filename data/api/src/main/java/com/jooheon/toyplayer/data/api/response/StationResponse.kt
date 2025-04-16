package com.jooheon.toyplayer.data.api.response

import android.annotation.SuppressLint
import android.content.Context
import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.domain.model.radio.RadioType
import kotlinx.serialization.Serializable

@Serializable
data class StationResponse(
    val type: String,
    val url: String?,
    val image: String,
    val channelName: String,
    val channelCode: String,
    val channelSubCode: String? = null,
    val updatedTime: Long? = null,
) {
    @SuppressLint("DiscouragedApi")
    fun toRadioData(context: Context): RadioData {
        val radioType = when (type) {
            "KBS" -> RadioType.KBS
            "SBS" -> RadioType.SBS
            "MBC" -> RadioType.MBC
            else -> RadioType.ETC(type)
        }
        val imageData = image.split("/")
        val resourceId = context.resources.getIdentifier(imageData.last(), imageData.first(), context.packageName)
        val imageUrl = "android.resource://${context.packageName}/$resourceId"

        return RadioData(
            type = radioType,
            url = url,
            imageUrl = imageUrl,
            channelName = channelName,
            channelCode = channelCode,
            channelSubCode = channelSubCode,
            updatedTime = updatedTime
        )
    }
}