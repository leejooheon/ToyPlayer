package com.jooheon.clean_architecture.presentation.view.setting.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.setting.SettingViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptySettingUseCase

@Composable
internal fun EqualizerScreen(
    navigator: NavController,
    viewModel: SettingViewModel = hiltViewModel(sharedViewModel())
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
            viewModel = SettingViewModel(EmptySettingUseCase())
        )
    }
}