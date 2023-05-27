package com.jooheon.clean_architecture.features.setting

import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.entity.music.SkipForwardBackward
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase

class EmptySettingUseCase: SettingUseCase {
    override suspend fun getLanguage(): Entity.SupportLaunguages {
        TODO("Not yet implemented")
    }

    override suspend fun setLanguage(language: Entity.SupportLaunguages) {
        TODO("Not yet implemented")
    }

    override suspend fun getTheme(): Entity.SupportThemes {
        TODO("Not yet implemented")
    }

    override suspend fun setTheme(theme: Entity.SupportThemes) {
        TODO("Not yet implemented")
    }

    override suspend fun getSkipForwardBackward(): SkipForwardBackward {
        TODO("Not yet implemented")
    }

    override suspend fun setSkipForwardBackward(skip: SkipForwardBackward) {
        TODO("Not yet implemented")
    }
}