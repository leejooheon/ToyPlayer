package com.jooheon.toyplayer.features.setting.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Forward5
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.jooheon.toyplayer.domain.entity.Entity
import com.jooheon.toyplayer.features.setting.R
import com.jooheon.toyplayer.core.strings.UiText

class SettingScreenItem(
    val event: SettingScreenEvent,
    val title: UiText,
    val value: UiText,
    val showValue: Boolean,
    val iconImageVector: ImageVector,
) {
    companion object {
        fun getSettingListItems(state: SettingScreenState): List<SettingScreenItem> {
            return listOf(
                SettingScreenItem(
                    event = SettingScreenEvent.OnLanguageScreenClick ,
                    title = UiText.StringResource(R.string.setting_language),
                    value = UiText.StringResource(R.string.setting_language_desc),
                    showValue = false,
                    iconImageVector = Icons.Outlined.Language
                ),
                SettingScreenItem(
                    event = SettingScreenEvent.OnThemeScreenClick,
                    title = UiText.StringResource(R.string.setting_theme),
                    value = parseThemeUiText(state.theme),
                    showValue = true,
                    iconImageVector = Icons.Outlined.WbSunny
                ),
                SettingScreenItem(
                    event = SettingScreenEvent.OnSkipDurationScreenClick(isShow = true),
                    title = UiText.StringResource(R.string.setting_skip_duration),
                    value =  UiText.StringResource(
                        resId = R.string.n_second,
                        args = arrayOf(state.skipDuration.toInteger())
                    ),
                    showValue = true,
                    iconImageVector = Icons.Outlined.Forward5
                ),
                SettingScreenItem(
                    event = SettingScreenEvent.OnEqualizerScreenClick,
                    title = UiText.StringResource(R.string.setting_equalizer),
                    value = UiText.DynamicString(""),
                    showValue = false,
                    iconImageVector = Icons.Outlined.Equalizer
                ),
            )
        }

        private fun parseThemeUiText(theme: Entity.SupportThemes) =  when(theme) {
            Entity.SupportThemes.AUTO -> UiText.StringResource(R.string.setting_follow_system)
            Entity.SupportThemes.DARK -> UiText.StringResource(R.string.setting_theme_dark)
            Entity.SupportThemes.LIGHT -> UiText.StringResource(R.string.setting_theme_light)
            Entity.SupportThemes.DYNAMIC_DARK -> UiText.StringResource(R.string.setting_theme_dynamic_dark)
            Entity.SupportThemes.DYNAMIC_LIGHT -> UiText.StringResource(R.string.setting_theme_dynamic_light)
        }
    }
}
