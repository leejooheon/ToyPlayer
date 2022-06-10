package com.jooheon.clean_architecture.presentation.view.home.repo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.theme.themes.CustomTheme
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.view.custom.RepositoryCard
import com.jooheon.clean_architecture.presentation.view.custom.RepositoryImage
import com.jooheon.clean_architecture.presentation.view.custom.offsetGradientBackground

private val CardWidth = 170.dp
private val CardPadding = 16.dp
private val gradientWidth
    @Composable
    get() = with(LocalDensity.current) {
        (3 * (CardWidth + CardPadding).toPx())
    }

@Composable
fun GithubRepositoryItem(
    owner: String,
    repositoryList: List<Entity.Repository>,
    modifier: Modifier = Modifier,
    onRepositoryClick: (Entity.Repository) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(start = 24.dp)
        ) {
            Text(
                text = owner,
                style = MaterialTheme.typography.bodyLarge,
                color = CustomTheme.colors.material3Colors.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    tint = Color.Black,
                    contentDescription = null
                )
            }
        }

        RepositoryItems(
            items = repositoryList,
            onRepositoryClick = onRepositoryClick
        )
    }
}

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
        modifier = modifier
            .size(
                width = 170.dp,
                height = 250.dp
            )
            .padding(bottom = 16.dp)
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
                color = CustomTheme.colors.material3Colors.onPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.created_at,
                style = MaterialTheme.typography.bodySmall,
                color = CustomTheme.colors.material3Colors.onPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun RepositoryItemPreview() {
    PreviewTheme(true) {
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