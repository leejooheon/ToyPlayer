package com.jooheon.clean_architecture.features.main

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase
import com.jooheon.clean_architecture.domain.usecase.setting.ThemeStateFlow
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.features.main.navigation.FullScreenNavigationHost
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicStateHolder
import com.jooheon.clean_architecture.features.setting.model.SettingScreenEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    val browser: MediaBrowser?
        get() = if (browserFuture.isDone || !browserFuture.isCancelled) browserFuture.get() else null

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

    override fun onStart() {
        super.onStart()
        initializeBrowser()
    }

    override fun onStop() {
        super.onStop()
        releaseBrowser()
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
    private fun initializeBrowser() {
        browserFuture =
            MediaBrowser.Builder(
                this,
                SessionToken(this, ComponentName(this, MusicService::class.java))
            ).buildAsync()

        browserFuture.addListener({
            onBrowserConnected()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun releaseBrowser() {
        musicStateHolder.onBrowserConnectionChanged(false)
        MediaBrowser.releaseFuture(browserFuture)
    }

    private fun onBrowserConnected() {
        musicStateHolder.onBrowserConnectionChanged(browser != null)
    }
}