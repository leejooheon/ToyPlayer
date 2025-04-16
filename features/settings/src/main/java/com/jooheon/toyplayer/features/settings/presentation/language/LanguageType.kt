package com.jooheon.toyplayer.features.settings.presentation.language

import androidx.appcompat.app.AppCompatDelegate
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import java.util.Locale

enum class LanguageType(val code: String) {
    KOREAN("ko"), ENGLISH("en");
    companion object {
        val default = ENGLISH

        fun current(): LanguageType {
            val languageCode = AppCompatDelegate
                .getApplicationLocales()
                .toLanguageTags()
                .defaultEmpty()
                .ifEmpty { Locale.getDefault().language }

            return LanguageType.entries.find { it.code == languageCode } ?: LanguageType.default
        }
    }

    fun stringResource() = when(this) {
        LanguageType.KOREAN -> Strings.korean
        LanguageType.ENGLISH -> Strings.english
    }
}