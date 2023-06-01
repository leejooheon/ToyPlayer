package com.jooheon.clean_architecture.features.main

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase
import com.jooheon.clean_architecture.domain.usecase.setting.ThemeStateFlow
import com.jooheon.clean_architecture.features.common.compose.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.features.main.navigation.FullScreenNavigationHost
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var musicControllerUseCase: MusicControllerUsecase
    private var serviceToken: MusicControllerUsecase.ServiceToken? = null

    @Inject
    lateinit var themeStateFlow: ThemeStateFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceToken = musicControllerUseCase.bindToService(this)

        setContent {
            AppContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        musicControllerUseCase.unbindToService(serviceToken)
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