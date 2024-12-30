package com.jooheon.toyplayer.features.setting.presentation.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.setting.presentation.SettingViewModel

@Composable
fun EqualizerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.onTertiary)
    ) {

    }
}

@Preview
@Composable
private fun PreviewEqualizerScreen() {
    ToyPlayerTheme {
        EqualizerScreen()
    }
}