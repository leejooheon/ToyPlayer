package com.jooheon.toyplayer.domain.usecase.setting

import com.jooheon.toyplayer.domain.entity.Entity
import com.jooheon.toyplayer.domain.entity.music.SkipForwardBackward
import com.jooheon.toyplayer.domain.repository.SettingRepository

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