package com.jooheon.clean_architecture.presentation.service.music.datasource

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCase
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LocalMusicPlayerDataSource @Inject constructor(
    private val musicUseCase: MusicUseCase,
): MusicPlayerDataSource() {
    override suspend fun getMusic() {
        state = State.STATE_INITIALIZING
        val uri = MusicUtil.localMusicStorageUri().toString()
        val resource = musicUseCase.getSongsSync(uri).first()
        when(resource) {
            is Resource.Success -> {
                allMusic = resource.value
                state = State.STATE_INITIALIZED
            }
            is Resource.Failure -> state = State.STATE_ERROR
            else -> state = State.STATE_ERROR
        }
    }
}