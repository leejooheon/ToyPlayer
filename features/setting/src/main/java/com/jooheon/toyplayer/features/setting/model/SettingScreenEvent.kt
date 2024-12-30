package com.jooheon.toyplayer.features.setting.model

import android.content.Context
import android.content.res.Resources
import android.os.LocaleList
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavController
import com.jooheon.toyplayer.domain.entity.Entity
import com.jooheon.toyplayer.domain.entity.music.SkipForwardBackward
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import java.util.Locale

sealed class SettingScreenEvent {
    data object OnBackClick: SettingScreenEvent()
    data object OnThemeScreenClick: SettingScreenEvent()
    data object OnLanguageScreenClick: SettingScreenEvent()
    data object OnEqualizerScreenClick: SettingScreenEvent()
    data class OnSkipDurationScreenClick(val isShow: Boolean): SettingScreenEvent()
    data class OnSkipDurationChanged(val data: SkipForwardBackward): SettingScreenEvent()
    data class OnLanguageChanged(val language: Entity.SupportLaunguages): SettingScreenEvent()
    data class OnThemeChanged(val theme: Entity.SupportThemes): SettingScreenEvent()

    companion object {
        fun changeLanguage(context: Context, language: Entity.SupportLaunguages) {
            val configuration = context.resources.configuration
            val locale = if(language == Entity.SupportLaunguages.AUTO) {
                ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
            } else {
                Locale(language.code)
            } ?: return

            // setup locale
            Locale.setDefault(locale)
            LocaleList.setDefault(LocaleList(locale))

            configuration.setLocales(LocaleList(locale))

            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            context.createConfigurationContext(configuration)
        }

        fun navigateTo(navController: NavController, route: ScreenNavigation) {
            if(route is ScreenNavigation.Back) {
                navController.popBackStack()
            } else {
                navController.navigate(route)
            }
        }
    }
}