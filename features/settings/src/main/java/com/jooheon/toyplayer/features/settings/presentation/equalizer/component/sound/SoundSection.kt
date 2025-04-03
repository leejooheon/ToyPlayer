package com.jooheon.toyplayer.features.settings.presentation.equalizer.component.sound

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.sound.component.ChannelBalanceControl
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.sound.component.CircularSlider
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.sound.component.VolumeSlider
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiEvent
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiState

internal fun Float.toDegrees() = Math.toDegrees(this.toDouble()).toFloat()

@Composable
internal fun SoundSection(
    soundGroup: EqualizerUiState.SoundGroup,
    onEvent: (EqualizerUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                CircularSlider(
                    title = UiText.StringResource(Strings.equalizer_bass_boost),
                    initialValue = soundGroup.bassBoost,
                    onValueChange = { onEvent.invoke(EqualizerUiEvent.OnBassBoostChanged(it)) },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                ChannelBalanceControl(
                    title = UiText.StringResource(Strings.equalizer_audio_channel_balance),
                    initialValue = soundGroup.channelBalance,
                    onValueChange = { onEvent.invoke(EqualizerUiEvent.OnChannelBalanceChanged(it)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            val (currentSystemVolume, maxSystemVolume) = soundGroup.systemVolume
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = UiText.StringResource(Strings.equalizer_player_volume_title).asString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                VolumeSlider(
                    title = UiText.StringResource(Strings.equalizer_system_volume_description),
                    volume = currentSystemVolume.toFloat(),
                    steps = maxSystemVolume,
                    valueRange = 0f..maxSystemVolume.toFloat(),
                    onVolumeChanged = { onEvent.invoke(EqualizerUiEvent.OnSystemVolumeChanged(it.toInt())) },
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(4.dp))

                VolumeSlider(
                    title = UiText.StringResource(Strings.equalizer_player_volume_description),
                    volume = soundGroup.playerVolume,
                    valueRange = 0f..1f,
                    onVolumeChanged = { onEvent.invoke(EqualizerUiEvent.OnPlayerVolumeChanged(it)) },
                    modifier = Modifier
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSoundSection() {
    val uiState = EqualizerUiState.preview
    ToyPlayerTheme {
        SoundSection(
            soundGroup = uiState.soundGroup,
            onEvent = {}
        )
    }
}