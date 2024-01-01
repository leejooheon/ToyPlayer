package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.cache

import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheSpan
import com.jooheon.clean_architecture.domain.usecase.music.list.MusicListUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.cache.model.MusicCacheScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.cache.model.MusicCacheScreenState
import com.jooheon.clean_architecture.features.musicservice.playback.PlaybackCacheManager
import com.jooheon.clean_architecture.features.musicservice.playback.PlaybackCacheManager.Companion.chunkLength
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@UnstableApi
class MusicCacheScreenViewModel @Inject constructor(
    private val musicListUseCase: MusicListUseCase,
    private val musicStateHolder: MusicStateHolder,
    private val playbackCacheManager: PlaybackCacheManager,
    musicControllerUseCase: MusicControllerUseCase,
): AbsMusicPlayerViewModel(musicControllerUseCase, musicStateHolder) {

    private val _musicCacheScreenState = MutableStateFlow(MusicCacheScreenState.default)
    val musicCacheScreenState = _musicCacheScreenState.asStateFlow()

    @UnstableApi
    private val cacheListener = object : Cache.Listener {
        override fun onSpanAdded(cache: Cache, span: CacheSpan) { loadData() }
        override fun onSpanRemoved(cache: Cache, span: CacheSpan) { loadData() }
        override fun onSpanTouched(cache: Cache, oldSpan: CacheSpan, newSpan: CacheSpan) {}
    }


    init {
        collectBrowserConnected()
        collectMediaItems()

    }

    override fun onCleared() {
        if(musicStateHolder.browserConnected.value) {
            playbackCacheManager.removeListener(
                key = MusicCacheScreenViewModel::class.java.simpleName,
                listener = cacheListener,
            )
        }
        super.onCleared()
    }

    private fun collectBrowserConnected() = viewModelScope.launch {
        musicStateHolder.browserConnected.collect { connected ->
            if(connected) {
                playbackCacheManager.addListener(
                    key = MusicCacheScreenViewModel::class.java.simpleName,
                    listener = cacheListener,
                )
            } else {
                playbackCacheManager.removeListener(
                    key = MusicCacheScreenViewModel::class.java.simpleName,
                    listener = cacheListener,
                )
            }
        }
    }

    private fun collectMediaItems() = viewModelScope.launch {
        musicStateHolder.mediaItems.collectLatest {
            loadData()
        }
    }

    private fun loadData() = viewModelScope.launch {
        if(!musicStateHolder.browserConnected.value) return@launch

        val library = musicListUseCase.assetSongList.firstOrNull() ?: emptyList()

        val cachedSongs = library.filter {
            playbackCacheManager.isCached(it.key(), 0, chunkLength)
        }

        _musicCacheScreenState.update {
            it.copy(
                songs = cachedSongs
            )
        }
    }

    fun dispatch(event: MusicCacheScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicCacheScreenEvent.PlaceHolder -> {}
            is MusicCacheScreenEvent.OnRefresh -> loadData()
        }
    }

    fun onMusicPlayerEvent(event: MusicPlayerEvent) = viewModelScope.launch {
        dispatch(event)
    }
}