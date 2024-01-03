package com.jooheon.toyplayer.domain.usecase.setting

import com.jooheon.toyplayer.domain.entity.Entity
import com.jooheon.toyplayer.domain.entity.music.SkipForwardBackward
import com.jooheon.toyplayer.domain.usecase.BaseUseCase

interface SettingUseCase: BaseUseCase {
    suspend fun getLanguage(): Entity.SupportLaunguages
    suspend fun setLanguage(language: Entity.SupportLaunguages)

    suspend fun getTheme(): Entity.SupportThemes
    suspend fun setTheme(theme: Entity.SupportThemes)

    suspend fun getSkipForwardBackward(): SkipForwardBackward
    suspend fun setSkipForwardBackward(skip: SkipForwardBackward)
}