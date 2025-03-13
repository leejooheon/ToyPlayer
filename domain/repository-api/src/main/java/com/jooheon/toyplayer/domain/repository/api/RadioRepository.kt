package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.MusicDataError
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.radio.RadioData

interface RadioRepository {
    suspend fun getRadioStationList(): Result<List<Song>, MusicDataError>
    suspend fun getRadioUrl(radioData: RadioData): Result<String, MusicDataError>
}