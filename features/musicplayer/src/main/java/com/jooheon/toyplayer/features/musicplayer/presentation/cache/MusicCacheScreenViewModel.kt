package com.jooheon.toyplayer.features.musicplayer.presentation.cache

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheSpan
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.cache.model.MusicCacheScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.cache.model.MusicCacheScreenState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(UnstableApi::class)
class MusicCacheScreenViewModel @Inject constructor(
//    private val playbackCacheManager: PlaybackCacheManager,
    musicStateHolder: MusicStateHolder,
    playerController: PlayerController,
    playbackEventUseCase: PlaybackEventUseCase
): AbsMusicPlayerViewModel(musicStateHolder, playerController, playbackEventUseCase) {

    private val _musicCacheScreenState = MutableStateFlow(MusicCacheScreenState.default)
    val musicCacheScreenState = _musicCacheScreenState.asStateFlow()

    private val _refreshChannel = Channel<Unit>()
    val refreshChannel = _refreshChannel.receiveAsFlow()

    @UnstableApi
    private val cacheListener = object : Cache.Listener {
        override fun onSpanAdded(cache: Cache, span: CacheSpan) { _refreshChannel.trySend(Unit) }
        override fun onSpanRemoved(cache: Cache, span: CacheSpan) { _refreshChannel.trySend(Unit) }
        override fun onSpanTouched(cache: Cache, oldSpan: CacheSpan, newSpan: CacheSpan) {}
    }

    init {
        addCacheListener()
    }

    override fun onCleared() {
//        playbackCacheManager.removeListener(
//            key = MusicCacheScreenViewModel::class.java.simpleName,
//            listener = cacheListener,
//        )
        super.onCleared()
    }

    private fun addCacheListener() = viewModelScope.launch {
//        playbackCacheManager.addListener(
//            key = MusicCacheScreenViewModel::class.java.simpleName,
//            listener = cacheListener,
//        )
    }

    fun loadData(context: Context) = viewModelScope.launch {
        val assetSongs = getMusicList(context, MediaId.AssetSongs)

//        val cachedSongs = assetSongs.filter {
//            playbackCacheManager.isCached(it.key(), 0, chunkLength)
//        }
//
//        _musicCacheScreenState.update {
//            it.copy(
//                songs = cachedSongs
//            )
//        }
    }

    fun dispatch(event: MusicCacheScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicCacheScreenEvent.OnRefresh -> loadData(event.context)
        }
    }

    fun onMusicPlayerEvent(event: MusicPlayerEvent) = viewModelScope.launch {
        dispatch(event)
    }
}