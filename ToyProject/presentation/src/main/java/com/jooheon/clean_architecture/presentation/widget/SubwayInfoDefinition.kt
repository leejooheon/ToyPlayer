package com.jooheon.clean_architecture.presentation.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

internal object SubwayInfoDefinition: GlanceStateDefinition<List<SubWayInfo>> {

    private const val DATA_STORE_FILENAME = "subway"
    private val Context.datastore by dataStore(DATA_STORE_FILENAME, StationInfoSerializer)

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<List<SubWayInfo>> = context.datastore

    override fun getLocation(context: Context, fileKey: String) =
        context.dataStoreFile(DATA_STORE_FILENAME)

    @OptIn(ExperimentalSerializationApi::class)
    object StationInfoSerializer: Serializer<List<SubWayInfo>> {
        override val defaultValue: List<SubWayInfo>
            get() = listOf(SubWayInfo.TEST_ONE, SubWayInfo.TEST_TWO)

        override suspend fun readFrom(input: InputStream): List<SubWayInfo> =
            Json.decodeFromStream(input)

        override suspend fun writeTo(t: List<SubWayInfo>, output: OutputStream) =
            Json.encodeToStream(t, output)
    }
}