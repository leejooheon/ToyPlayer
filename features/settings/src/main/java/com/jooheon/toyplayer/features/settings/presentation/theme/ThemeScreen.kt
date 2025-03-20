package com.jooheon.toyplayer.features.settings.presentation.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.LocalDarkTheme
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Drawables
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.commonui.components.CustomTopAppBar
import com.jooheon.toyplayer.features.settings.presentation.theme.component.ThemeCard

@Composable
fun ThemeScreen(
    darkTheme: Boolean = LocalDarkTheme.current,
    onChangeDarkTheme: (Boolean) -> Unit,
    onBackClick: () -> Unit,
) {
    ThemeScreenInternal(
        darkTheme = darkTheme,
        onChangeDarkTheme = onChangeDarkTheme,
        onBackClick = onBackClick
    )
}

@Composable
private fun ThemeScreenInternal(
    darkTheme: Boolean,
    onChangeDarkTheme: (Boolean) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = UiText.StringResource(Strings.setting_theme).asString(),
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val cardModifier = Modifier.weight(1f)
                        ThemeCard(
                            selected = darkTheme.not(),
                            titleRes = Strings.light_mode,
                            imageRes = Drawables.img_light_mode,
                            onClick = { onChangeDarkTheme(false) },
                            modifier = cardModifier,
                        )
                        ThemeCard(
                            selected = darkTheme,
                            titleRes = Strings.dark_mode,
                            imageRes = Drawables.img_dark_mode,
                            onClick = { onChangeDarkTheme(true) },
                            modifier = cardModifier,
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun PreviewThemeScreen() {
    ToyPlayerTheme {
        ThemeScreenInternal(
            darkTheme = false,
            onBackClick = {},
            onChangeDarkTheme = {},
        )
    }
}