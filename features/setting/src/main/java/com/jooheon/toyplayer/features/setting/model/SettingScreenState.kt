package com.jooheon.toyplayer.features.setting.model

import com.jooheon.toyplayer.domain.entity.Entity
import com.jooheon.toyplayer.domain.entity.music.SkipForwardBackward
import com.jooheon.toyplayer.features.common.utils.VersionUtil

data class SettingScreenState(
    val language: Entity.SupportLaunguages,
    val theme: Entity.SupportThemes,
    val skipDuration: SkipForwardBackward,
    val showSkipDurationDialog: Boolean,
) {
    companion object {
        val default = SettingScreenState(
            language = Entity.SupportLaunguages.AUTO,
            theme = Entity.SupportThemes.DYNAMIC_LIGHT,
            skipDuration = SkipForwardBackward.FIVE_SECOND,
            showSkipDurationDialog = false,
        )

        fun showableTheme(theme: Entity.SupportThemes): Boolean {
            val supportDynamicColor = VersionUtil.hasS()
            return if(theme.code.contains("dynamic") ) {
                supportDynamicColor
            } else {
                true
            }
        }
    }
}