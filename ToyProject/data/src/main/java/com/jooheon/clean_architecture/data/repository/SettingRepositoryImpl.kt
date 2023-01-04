package com.jooheon.clean_architecture.data.repository

import com.jooheon.clean_architecture.data.local.AppPreferences
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.repository.SettingRepository

class SettingRepositoryImpl(
    private val appPreferences: AppPreferences
): SettingRepository {
    override fun getLanguage() = appPreferences.language
    override fun setLanguage(language: Entity.SupportLaunguages) {
        appPreferences.language = language
    }

    override fun getTheme() = appPreferences.theme
    override fun setTheme(theme: Entity.SupportThemes) {
        appPreferences.theme = theme
    }

    override fun getSkipForwardBackward() = appPreferences.skipForwardBackward
    override fun setSkipForwardBackward(skipForwardBackward: Entity.SkipForwardBackward) {
        appPreferences.skipForwardBackward = skipForwardBackward
    }
}