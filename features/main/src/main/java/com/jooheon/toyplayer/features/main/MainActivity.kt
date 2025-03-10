package com.jooheon.toyplayer.features.main

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.main.presentation.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    @Inject
//    lateinit var themeStateFlow: ThemeStateFlow
//
//    @Inject
//    lateinit var settingUseCase: SettingUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(LifecycleTAG).d("onCreate")

//        lifecycleScope.launch {
//            SettingScreenEvent.changeLanguage(
//                context = this@MainActivity,
//                language = settingUseCase.getLanguage()
//            )
//        }

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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    companion object {
        private const val LifecycleTAG = "ActivityLifecycle"
    }
}