package com.jooheon.toyplayer.features.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.jooheon.toyplayer.domain.usecase.setting.SettingUseCase
import com.jooheon.toyplayer.domain.usecase.setting.ThemeStateFlow
import com.jooheon.toyplayer.features.common.compose.theme.themes.ApplicationTheme
import com.jooheon.toyplayer.features.main.navigation.FullScreenNavigationHost
import com.jooheon.toyplayer.features.musicservice.usecase.MusicStateHolder
import com.jooheon.toyplayer.features.setting.model.SettingScreenEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeStateFlow: ThemeStateFlow

    @Inject
    lateinit var settingUseCase: SettingUseCase

    @Inject
    lateinit var musicStateHolder: MusicStateHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

        lifecycleScope.launch {
            SettingScreenEvent.changeLanguage(
                context = this@MainActivity,
                language = settingUseCase.getLanguage()
            )
        }

        setContent {
            AppContent()
        }
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
    }

    @Composable
    private fun AppContent() {
        val themeState = themeStateFlow.themeState.collectAsState()
//        val theme = settingViewModel.themeState.collectAsState()
        ApplicationTheme(themeState.value) {
            FullScreenNavigationHost(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}