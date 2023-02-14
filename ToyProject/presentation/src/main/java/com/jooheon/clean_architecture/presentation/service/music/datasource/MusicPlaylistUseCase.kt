package com.jooheon.clean_architecture.presentation.service.music.datasource

import android.util.Log
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCase
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MusicPlaylistUseCase @Inject constructor(
    private val musicUseCase: MusicUseCase,
) {
    var allMusic: List<Entity.Song> = emptyList()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = State.STATE_CREATED
        set(value) {
            if (value == State.STATE_INITIALIZED || value == State.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach {
                        it(value == State.STATE_INITIALIZED)
                    }
                }
            } else field = value
        }

    fun loadMusic(scope: CoroutineScope): MusicPlaylistUseCase {
        state = State.STATE_INITIALIZING

        val uri = MusicUtil.localMusicStorageUri().toString()
        musicUseCase.getSongs(uri).onEach { resource ->
            when(resource) {
                is Resource.Success -> {
                    Log.d("asd", "singleton test ${this}")
                    allMusic = resource.value
                    state = State.STATE_INITIALIZED
                }
                is Resource.Failure -> state = State.STATE_ERROR
                is Resource.Loading -> state = State.STATE_INITIALIZING
                is Resource.Default -> { /** Nothing **/ }
            }
        }.launchIn(scope)

        return this
    }

    fun whenReady(onReady: (Boolean) -> Unit): Boolean =
        if (state == State.STATE_CREATED || state == State.STATE_INITIALIZING) {
            onReadyListeners += onReady
            false
        } else {
            onReady(state == State.STATE_INITIALIZED)
            true
        }

    enum class State {
        STATE_CREATED,
        STATE_INITIALIZING,
        STATE_INITIALIZED,
        STATE_ERROR
    }
}