package com.jooheon.clean_architecture.features.github.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.common.compose.theme.themes.CustomTheme
import com.jooheon.clean_architecture.features.github.main.CardPadding
import com.jooheon.clean_architecture.features.github.main.CardWidth


@Composable
fun RepositoryItems(
    items: List<Entity.Repository>,
    modifier: Modifier = Modifier,
    onRepositoryClick: (Entity.Repository) -> Unit
) {
    val index = 5
    val scroll = rememberScrollState(0)
    val gradient = when ((index / 2) % 2) {
        0 -> CustomTheme.colors.gradient6_1
        else -> CustomTheme.colors.gradient6_2
    }
    // The Cards show a gradient which spans 3 cards and scrolls with parallax.
    val gradientWidth = with(LocalDensity.current) {
        (6 * (CardWidth + CardPadding).toPx())
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
    ) {
        itemsIndexed(items) { index, item ->
            RepositoryItem(
                item,
                onRepositoryClick,
                index,
                gradient,
                gradientWidth,
                scroll.value
            )
        }
    }
}