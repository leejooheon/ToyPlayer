package com.jooheon.toyplayer.features.settings.presentation.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.commonui.components.CustomTopAppBar
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.equalizer.EqualizerSection
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.sound.SoundSection
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiEvent
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiState

@Composable
fun EqualizerScreen(
    onBack: () -> Unit,
    viewModel: EqualizerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EqualizerScreenInternal(
        uiState = uiState,
        onEvent = viewModel::dispatch,
        onBackClick = onBack,
    )
}

@Composable
private fun EqualizerScreenInternal(
    uiState: EqualizerUiState,
    onEvent: (EqualizerUiEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        Strings.equalizer_tab_eq,
        Strings.equalizer_tab_sound
    ).map {
        UiText.StringResource(it).asString()
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = UiText.StringResource(Strings.setting_equalizer).asString(),
                onClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { onEvent.invoke(EqualizerUiEvent.OnSettingClick(context)) },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .bounceClick { onEvent.invoke(EqualizerUiEvent.OnSettingClick(context)) },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(Strings.title_settings),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title
                                    )
                                }
                            )
                        }
                    }

                    when (selectedTabIndex) {
                        0 -> {
                            EqualizerSection(
                                presetGroups = uiState.presetGroups,
                                selectedPreset = uiState.selectedPreset,
                                onEvent = onEvent,
                                modifier = Modifier
                            )
                        }
                        1 -> {
                            SoundSection(
                                soundGroup = uiState.soundGroup,
                                onEvent = onEvent,
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
@Preview
private fun PreviewEqualizerScreen() {
    ToyPlayerTheme {
        EqualizerScreenInternal(
            uiState = EqualizerUiState.preview,
            onEvent = {},
            onBackClick = {},
        )
    }
}