package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.MusicDataError
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.domain.repository.api.RadioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RadioUseCase @Inject constructor(
    private val radioRepository: RadioRepository,
) {
    suspend fun getRadioStationList(): List<Song> = withContext(Dispatchers.IO) {
        val result = radioRepository.getRadioStationList()
        return@withContext when(result) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
    }

    suspend fun getRadioUrl(radioData: RadioData): Result<String, MusicDataError> = withContext(Dispatchers.IO) {
        return@withContext radioRepository.getRadioUrl(radioData)
    }
}