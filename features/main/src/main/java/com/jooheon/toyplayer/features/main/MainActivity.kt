package com.jooheon.toyplayer.features.main

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
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
//        lifecycleScope.launch {
//            SettingScreenEvent.changeLanguage(
//                context = this@MainActivity,
//                language = settingUseCase.getLanguage()
//            )
//        }

        setContent {
            ToyPlayerTheme {
                MainScreen()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}