package com.jooheon.toyplayer.features.setting.model

import com.jooheon.toyplayer.domain.entity.SupportThemes
import com.jooheon.toyplayer.domain.entity.music.SkipForwardBackward
import com.jooheon.toyplayer.features.common.utils.VersionUtil

data class SettingScreenState(
    val theme: SupportThemes,
    val skipDuration: SkipForwardBackward,
    val showSkipDurationDialog: Boolean,
) {
    companion object {
        val default = SettingScreenState(
            theme = SupportThemes.DYNAMIC_LIGHT,
            skipDuration = SkipForwardBackward.FIVE_SECOND,
            showSkipDurationDialog = false,
        )

        fun showableTheme(theme: SupportThemes): Boolean {
            val supportDynamicColor = VersionUtil.hasS()
            return if(theme.code.contains("dynamic") ) {
                supportDynamicColor
            } else {
                true
            }
        }
    }
}