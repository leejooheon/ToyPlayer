package com.jooheon.clean_architecture.domain.usecase.setting

import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase

interface SettingUseCase: BaseUseCase {
    suspend fun getLanguage(): Entity.SupportLaunguages
    suspend fun setLanguage(language: Entity.SupportLaunguages)

    suspend fun getTheme(): Entity.SupportThemes
    suspend fun setTheme(theme: Entity.SupportThemes)

    suspend fun getSkipForwardBackward(): Entity.SkipForwardBackward
    suspend fun setSkipForwardBackward(skip: Entity.SkipForwardBackward)
}