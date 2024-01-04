package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.datasource.local.AppPreferences
import com.jooheon.toyplayer.domain.entity.Entity
import com.jooheon.toyplayer.domain.entity.music.SkipForwardBackward
import com.jooheon.toyplayer.domain.repository.SettingRepository

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
    override fun setSkipForwardBackward(skipForwardBackward: SkipForwardBackward) {
        appPreferences.skipForwardBackward = skipForwardBackward
    }
}