package com.jooheon.toyplayer.features.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.main.presentation.MainScreen
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

    @Composable
    private fun AppContent() {
//        val themeState = themeStateFlow.themeState.collectAsState()
//        val theme = settingViewModel.themeState.collectAsState()

        ToyPlayerTheme {
            MainScreen()
        }
    }

    companion object {
        private const val LifecycleTAG = "ActivityLifecycle"
    }
}