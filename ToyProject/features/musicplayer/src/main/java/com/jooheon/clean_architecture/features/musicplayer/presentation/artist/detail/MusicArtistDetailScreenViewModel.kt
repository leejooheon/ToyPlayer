package com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.entity.music.Artist
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail.model.MusicArtistDetailScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail.model.MusicArtistDetailScreenState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicArtistDetailScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUsecase
): BaseViewModel() {
    override val TAG = MusicArtistDetailScreenViewModel::class.java.simpleName

    private val _musicArtistDetailScreenState = MutableStateFlow(MusicArtistDetailScreenState.default)
    val musicArtistDetailScreenState = _musicArtistDetailScreenState.asStateFlow()

    private val _navigateTo = Channel<String>()
    val navigateTo = _navigateTo.receiveAsFlow()

    fun init(artist: Artist) = viewModelScope.launch {
        _musicArtistDetailScreenState.update {
            it.copy(artist = artist)
        }
    }
    fun dispatch(event: MusicArtistDetailScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicArtistDetailScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back.route)
            is MusicArtistDetailScreenEvent.OnSongClick -> {
                musicControllerUsecase.onPlay(event.song)
            }
        }
    }
}
