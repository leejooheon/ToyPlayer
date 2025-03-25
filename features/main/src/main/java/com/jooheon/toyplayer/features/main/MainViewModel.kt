package com.jooheon.toyplayer.features.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class MainViewModel @Inject constructor(
    private val defaultSettingsUseCase: DefaultSettingsUseCase,
    private val playerController: PlayerController,
): ViewModel() {
    val isDarkTheme = defaultSettingsUseCase.flowIsDarkTheme()

    fun updateIsDarkTheme(isDarkTheme: Boolean) =
        viewModelScope.launch {
            defaultSettingsUseCase.updateIsDarkTheme(isDarkTheme)
        }

    fun onPermissionGranted(context: Context) = viewModelScope.launch {
        playerController.getMusicListFuture(
            context = context,
            mediaId = MediaId.Root,
            listener = { }
        )
    }
}