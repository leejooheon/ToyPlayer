package com.jooheon.clean_architecture.domain.usecase.setting

import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.entity.music.SkipForwardBackward
import com.jooheon.clean_architecture.domain.repository.SettingRepository

class SettingUseCaseImpl(
    private val settingRepository: SettingRepository
): SettingUseCase {
    override suspend fun getLanguage(): Entity.SupportLaunguages {
        return settingRepository.getLanguage()
    }

    override suspend fun setLanguage(language: Entity.SupportLaunguages) {
        settingRepository.setLanguage(language)
    }

    override suspend fun getTheme() = settingRepository.getTheme()

    override suspend fun setTheme(theme: Entity.SupportThemes) {
        settingRepository.setTheme(theme)
    }

    override suspend fun getSkipForwardBackward() = settingRepository.getSkipForwardBackward()

    override suspend fun setSkipForwardBackward(skip: SkipForwardBackward) {
        settingRepository.setSkipForwardBackward(skip)
    }

}