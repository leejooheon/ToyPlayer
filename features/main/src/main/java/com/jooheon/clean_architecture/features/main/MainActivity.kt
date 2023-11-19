package com.jooheon.clean_architecture.features.main

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase
import com.jooheon.clean_architecture.domain.usecase.setting.ThemeStateFlow
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.features.main.navigation.FullScreenNavigationHost
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.setting.model.SettingScreenEvent
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

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}