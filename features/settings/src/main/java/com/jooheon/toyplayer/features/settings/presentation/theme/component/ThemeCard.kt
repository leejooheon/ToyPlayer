package com.jooheon.toyplayer.features.settings.presentation.theme.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Drawables
import com.jooheon.toyplayer.core.resources.Strings


@Composable
internal fun ThemeCard(
    modifier: Modifier = Modifier,
    selected: Boolean,
    @StringRes titleRes: Int,
    @DrawableRes imageRes: Int,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            onClick = onClick,
        ) {
            Image(
                painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.aspectRatio(1f)
            )
        }

        Text(
            text = stringResource(id = titleRes),
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )

        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.onSurface,
                unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        )
    }
}

@Preview
@Composable
private fun DarkModeThemeCardPreview() {
    ToyPlayerTheme {
        ThemeCard(
            selected = true,
            titleRes = Strings.dark_mode,
            imageRes = Drawables.img_dark_mode,
            onClick = { },
        )
    }
}

@Preview
@Composable
private fun LightModeThemeCardPreview() {
    ToyPlayerTheme {
        ThemeCard(
            selected = false,
            titleRes =  Strings.light_mode,
            imageRes = Drawables.img_light_mode,
            onClick = { },
        )
    }
}
