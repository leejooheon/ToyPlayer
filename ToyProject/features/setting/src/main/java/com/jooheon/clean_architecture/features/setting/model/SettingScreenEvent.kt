package com.jooheon.clean_architecture.features.setting.model

import android.content.Context
import android.content.res.Resources
import android.os.LocaleList
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavController
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.common.compose.observeWithLifecycle
import java.util.Locale
enum class SettingScreenEvent {
    GoToBack, GoToLanguageScreen, GoToThemeScreen, GoToEqualizer, ShowSkipDurationDialog,
    SkipDurationChanged, LanguageChanged, ThemeChanged;

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

        fun navigateTo(navController: NavController, route: String) {
            if(route == ScreenNavigation.Back.route) {
                navController.popBackStack()
            } else {
                navController.navigate(route)
            }
        }
    }
}