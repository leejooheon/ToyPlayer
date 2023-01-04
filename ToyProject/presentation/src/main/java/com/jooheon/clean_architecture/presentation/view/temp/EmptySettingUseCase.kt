package com.jooheon.clean_architecture.presentation.view.temp

import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase
import kotlinx.coroutines.flow.Flow

class EmptySettingUseCase: SettingUseCase {
    override suspend fun getLanguage(): Flow<Entity.SupportLaunguages> {
        TODO("Not yet implemented")
    }

    override suspend fun setLanguage(language: Entity.SupportLaunguages) {
        TODO("Not yet implemented")
    }

    override suspend fun getTheme(): Flow<Entity.SupportThemes> {
        TODO("Not yet implemented")
    }

    override suspend fun setTheme(theme: Entity.SupportThemes) {
        TODO("Not yet implemented")
    }

    override suspend fun getSkipForwardBackward(): Flow<Entity.SkipForwardBackward> {
        TODO("Not yet implemented")
    }

    override suspend fun setSkipForwardBackward(theme: Entity.SkipForwardBackward) {
        TODO("Not yet implemented")
    }
}