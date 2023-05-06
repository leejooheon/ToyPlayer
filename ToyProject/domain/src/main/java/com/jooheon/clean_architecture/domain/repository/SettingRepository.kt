package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.entity.music.SkipForwardBackward
import kotlinx.coroutines.flow.Flow

interface SettingRepository: BaseRepository {
    fun getLanguage(): Entity.SupportLaunguages
    fun setLanguage(language: Entity.SupportLaunguages)

    fun getTheme(): Entity.SupportThemes
    fun setTheme(theme: Entity.SupportThemes)

    fun getSkipForwardBackward(): SkipForwardBackward
    fun setSkipForwardBackward(skipForwardBackward: SkipForwardBackward)
}