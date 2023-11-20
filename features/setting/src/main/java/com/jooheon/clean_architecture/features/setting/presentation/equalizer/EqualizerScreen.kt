package com.jooheon.clean_architecture.features.setting.presentation.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.setting.presentation.SettingViewModel

@Composable
fun EqualizerScreen(
    navigator: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
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
    val context = LocalContext.current
    PreviewTheme(false) {
        EqualizerScreen(
            navigator = NavController(context),
//            viewModel = SettingViewModel(EmptySettingUseCase())
        )
    }
}