package com.jooheon.toyplayer.features.setting.model

import androidx.navigation.NavController
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.domain.model.SupportThemes
import com.jooheon.toyplayer.domain.model.music.SkipForwardBackward

sealed class SettingScreenEvent {
    data object OnBackClick: SettingScreenEvent()
    data object OnThemeScreenClick: SettingScreenEvent()
    data object OnLanguageScreenClick: SettingScreenEvent()
    data object OnEqualizerScreenClick: SettingScreenEvent()
    data class OnSkipDurationScreenClick(val isShow: Boolean): SettingScreenEvent()
    data class OnSkipDurationChanged(val data: SkipForwardBackward): SettingScreenEvent()
    data class OnThemeChanged(val theme: SupportThemes): SettingScreenEvent()

    companion object {
        fun navigateTo(navController: NavController, route: ScreenNavigation) {
            if(route is ScreenNavigation.Back) {
                navController.popBackStack()
            } else {
                navController.navigate(route)
            }
        }
    }
}