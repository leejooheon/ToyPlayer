package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.music.RemoteMusicDataSource
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.MusicDataError
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.domain.model.radio.RadioType
import com.jooheon.toyplayer.domain.repository.api.RadioRepository

class RadioRepositoryImpl(
    private val dataSource: RemoteMusicDataSource,
): RadioRepository {
    override suspend fun getRadioStationList(): Result<List<Song>, MusicDataError> {
        val stations = dataSource.getRadioStationList()

        return if (stations.isEmpty()) {
            Result.Error(MusicDataError.Empty)
        } else {
            val songs = stations.mapIndexed { index, radioData -> radioData.toSong(index) }
            Result.Success(songs)
        }
    }

    override suspend fun getRadioUrl(radioData: RadioData): Result<String, MusicDataError> {
        return when(radioData.type) {
            is RadioType.Kbs -> dataSource.getKbsRadioUrl(radioData)
            is RadioType.Sbs -> dataSource.getSbsRadioUrl(radioData)
            is RadioType.Mbc -> dataSource.getMbcRadioUrl(radioData)
            is RadioType.Etc -> dataSource.getEtcRadioUrl(radioData)
        }
    }
}