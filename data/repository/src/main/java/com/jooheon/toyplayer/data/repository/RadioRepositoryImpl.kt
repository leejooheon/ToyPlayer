package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.music.RemoteMusicDataSource
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.domain.model.radio.RadioType
import com.jooheon.toyplayer.domain.repository.api.RadioRepository

class RadioRepositoryImpl(
    private val dataSource: RemoteMusicDataSource,
): RadioRepository {
    override suspend fun getRadioStationList(): Result<List<Song>, PlaybackDataError> {
        val stations = dataSource.getRadioStationList()

        return if (stations.isEmpty()) {
            Result.Error(PlaybackDataError.Empty)
        } else {
            val songs = stations.mapIndexed { index, radioData -> radioData.toSong(index) }
            Result.Success(songs)
        }
    }

    override suspend fun getRadioUrl(radioData: RadioData): Result<String, PlaybackDataError> {
        return when(radioData.type) {
            is RadioType.KBS -> dataSource.getKbsRadioUrl(radioData)
            is RadioType.SBS -> dataSource.getSbsRadioUrl(radioData)
            is RadioType.MBC -> dataSource.getMbcRadioUrl(radioData)
            is RadioType.ETC -> dataSource.getEtcRadioUrl(radioData)
        }
    }
}