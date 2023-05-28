package com.jooheon.clean_architecture.features.github.main.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.common.compose.theme.themes.CustomTheme
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.github.main.presentation.main.CardPadding
import com.jooheon.clean_architecture.features.github.main.presentation.main.CardWidth
import com.jooheon.clean_architecture.features.github.extensions.offsetGradientBackground

@Composable
fun RepositoryItem(
    item: Entity.Repository,
    onRepositoryClick: (Entity.Repository) -> Unit,
    index: Int,
    gradient: List<Color>,
    gradientWidth: Float,
    scroll: Int,
    modifier: Modifier = Modifier
) {
    val left = index * with(LocalDensity.current) {
        (CardWidth + CardPadding).toPx()
    }
    RepositoryCard(
        modifier = modifier.size(
            width = 170.dp,
            height = 250.dp
        ).padding(bottom = 16.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = { onRepositoryClick(item) })
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
            ) {
                val gradientOffset = left - (scroll / 3f)
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .offsetGradientBackground(gradient, gradientWidth, gradientOffset)
                )
                RepositoryImage(
                    imageUrl = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.BottomCenter)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.created_at,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun RepositoryItemPreview() {
    PreviewTheme(false) {
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
        val item: Entity.Repository = Entity.Repository(
            name = "name",
            id = "id",
            created_at = "created_at",
            html_url = "https://asd.com",
            imageUrl = "image"
        )

        RepositoryItem(
            item,
            {},
            index,
            gradient,
            gradientWidth,
            scroll.value
        )
    }
}