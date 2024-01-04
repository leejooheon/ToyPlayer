package com.jooheon.toyplayer.domain.repository

import com.jooheon.toyplayer.domain.entity.Entity
import com.jooheon.toyplayer.domain.entity.music.SkipForwardBackward

interface SettingRepository: BaseRepository {
    fun getLanguage(): Entity.SupportLaunguages
    fun setLanguage(language: Entity.SupportLaunguages)

    fun getTheme(): Entity.SupportThemes
    fun setTheme(theme: Entity.SupportThemes)

    fun getSkipForwardBackward(): SkipForwardBackward
    fun setSkipForwardBackward(skipForwardBackward: SkipForwardBackward)
}