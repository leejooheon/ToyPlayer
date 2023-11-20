package com.jooheon.clean_architecture.features.setting.model

import android.os.Build
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.entity.music.SkipForwardBackward
import com.jooheon.clean_architecture.toyproject.features.common.utils.VersionUtil

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