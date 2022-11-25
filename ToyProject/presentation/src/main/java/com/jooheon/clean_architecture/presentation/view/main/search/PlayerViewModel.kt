package com.jooheon.clean_architecture.presentation.view.main.search

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import com.jooheon.clean_architecture.presentation.utils.VersionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicUseCase: MusicUseCase
): BaseViewModel() {
    override val TAG = PlayerViewModel::class.java.simpleName

    private val _songList = mutableStateOf<List<Entity.Song>>(emptyList())
    val songList = _songList

    fun fetchAlbums() {
        val uri = getUri()
        musicUseCase.getAlbums(uri.toString()).onEach { resource ->
            handleResponse(resource)
        }.launchIn(viewModelScope)
    }

    fun fetchSongs() {
        val uri = getUri()
        musicUseCase.getSongs(uri.toString()).onEach { resource ->
            handleResponse(resource)

            if(resource is Resource.Success) {
                _songList.value = resource.value

                resource.value.forEach {
                    Log.d(TAG, "song title: ${it.title}")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getUri(): Uri {
        val uri = if (VersionUtils.hasQ()) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        return uri
    }
}