package com.jooheon.toyplayer.features.settings.presentation.equalizer.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import timber.log.Timber

@Composable
internal fun EqualizerSliderColumn(
    centerFrequencies: List<Float>,
    gains: List<Float>,
    onGainsChange: (List<Float>) -> Unit,
    modifier: Modifier = Modifier
) {
    Timber.d("gains: $gains")
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(centerFrequencies) { index, frequency ->
            Timber.d("freq: $frequency, gain: ${gains.getOrNull(index).defaultZero()}")

            EqualizerSlider(
                frequency = frequency,
                gain = gains.getOrNull(index).defaultZero(),
                onGainChange = { gain ->
                    if(gains.getOrNull(index) == null) return@EqualizerSlider
                    if(gains.getOrNull(index) == gain) return@EqualizerSlider

                    val updatedGains = gains
                        .toMutableList()
                        .apply { this[index] = gain }
                    onGainsChange(updatedGains)
                },
                modifier = Modifier
            )
        }
    }
}
