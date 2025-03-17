package com.jooheon.toyplayer.features.main

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.common.compose.TouchEventController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        lifecycleScope.launch {
            TouchEventController.sendEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }
}