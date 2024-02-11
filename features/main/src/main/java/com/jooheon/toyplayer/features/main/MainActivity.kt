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
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.common.compose.theme.themes.ApplicationTheme
import com.jooheon.toyplayer.features.main.navigation.FullScreenNavigationHost
import com.jooheon.toyplayer.features.setting.model.SettingScreenEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeStateFlow: ThemeStateFlow

    @Inject
    lateinit var settingUseCase: SettingUseCase

    @Inject
    lateinit var playerController: PlayerController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(LifecycleTAG).d("onCreate")

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

    override fun onStart() {
        super.onStart()
        Timber.tag(LifecycleTAG).d("onStart")
        playerController.connect(this)
    }

    override fun onStop() {
        Timber.tag(LifecycleTAG).d("onStop")
        playerController.release()
        super.onStop()
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
        private const val LifecycleTAG = "ActivityLifecycle"
    }
}