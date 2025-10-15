package com.jooheon.toyplayer.features.main

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.EntryProviderInstaller
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.features.common.controller.TouchEventController
import com.jooheon.toyplayer.features.common.utils.VersionUtil
import com.jooheon.toyplayer.features.upnp.DlnaPlayerController
import com.jooheon.toyplayer.features.upnp.DlnaServiceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var entryProviderBuilders: Set<@JvmSuppressWildcards EntryProviderInstaller>

    @Inject
    lateinit var dlnaServiceManager: DlnaServiceManager

    @Inject
    lateinit var dlnaPlayerController: DlnaPlayerController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeConfig()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle(false, this)
            var mute by remember { mutableStateOf(false) }

            ToyPlayerTheme(isDarkTheme) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Button(onClick = {
                            val remoteUrl = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3"
                            dlnaPlayerController.setUri(remoteUrl)
                        }) {
                            Text("play")
                        }
                        Button(onClick = { dlnaPlayerController.pause() }) {
                            Text("Pause")
                        }
                        Button(onClick = {
                            mute = !mute
                            dlnaPlayerController.setMute(mute)
                        }) {
                            Text("Mute")
                        }
                        Button(onClick = { dlnaPlayerController.getCurrentTransportActions() }) {
                            Text("Actions")
                        }
                    }
                }
            }
        }

        dlnaServiceManager.bindService(this)
        observeDlnaService()
    }

    override fun onDestroy() {
        dlnaServiceManager.unbindService(this)
        super.onDestroy()
    }

    private fun observeDlnaService() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dlnaServiceManager.rendererFlow.collect {
                    if(it.isEmpty()) {
                        dlnaPlayerController.disConnect()
                    } else {
                        dlnaPlayerController.connect(it.first())
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        lifecycleScope.launch {
            TouchEventController.sendEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setEdgeToEdgeConfig() {
        enableEdgeToEdge()
        if (VersionUtil.hasQ()) {
            // Force the 3-button navigation bar to be transparent
            // See: https://developer.android.com/develop/ui/views/layout/edge-to-edge#create-transparent
            window.isNavigationBarContrastEnforced = false
        }
    }
}