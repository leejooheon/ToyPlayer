package com.jooheon.clean_architecture.domain.usecase.setting

import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow

interface SettingUseCase: BaseUseCase {
    suspend fun getLanguage(): Flow<Entity.SupportLaunguages>
    suspend fun setLanguage(language: Entity.SupportLaunguages)

    suspend fun getTheme(): Flow<Entity.SupportThemes>
    suspend fun setTheme(theme: Entity.SupportThemes)

    suspend fun getSkipForwardBackward(): Flow<Entity.SkipForwardBackward>
    suspend fun setSkipForwardBackward(theme: Entity.SkipForwardBackward)
}