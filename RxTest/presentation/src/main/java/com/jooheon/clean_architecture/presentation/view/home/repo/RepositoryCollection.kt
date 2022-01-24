package com.jooheon.clean_architecture.presentation.view.home.repo

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jooheon.clean_architecture.domain.entity.Entity
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.jooheon.clean_architecture.presentation.theme.CustomTheme
import com.jooheon.clean_architecture.presentation.view.custom.CustomSurface
import com.jooheon.clean_architecture.presentation.view.custom.RepositoryCard
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.view.custom.offsetGradientBackground

private val CardWidth = 170.dp
private val CardPadding = 16.dp
private val gradientWidth
    @Composable
    get() = with(LocalDensity.current) {
        (3 * (CardWidth + CardPadding).toPx())
    }

@Composable
fun RepositoryCollection(
    owner: String,
    repositoryList: List<Entity.Repository>,
    modifier: Modifier = Modifier,
    onRepositoryClick: (String) -> Unit
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
                style = MaterialTheme.typography.h6,
                color = Color.Black,
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
    onRepositoryClick: (String) -> Unit
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
    onRepositoryClick: (String) -> Unit,
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
                .clickable(onClick = { onRepositoryClick(item.id) })
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
                SnackImage(
                    imageUrl = tempImages.get(index).imageUrl,
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
                style = MaterialTheme.typography.h6,
                color = CustomTheme.colors.textSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.created_at,
                style = MaterialTheme.typography.caption,
                color = CustomTheme.colors.textHelp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun SnackImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp
) {
    Log.d("hh", "imageUrl:" + imageUrl)
    CustomSurface(
        color = Color.LightGray,
        elevation = elevation,
        shape = CircleShape,
        modifier = modifier
    ) {
        Image(
            painter = rememberImagePainter(
                data = imageUrl,
                builder = {
                    crossfade(true)
                    placeholder(drawableResId = R.drawable.ic_logo_github)
                }
            ),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}


val tempImages: List<Entity.TempImage> = listOf(
    Entity.TempImage(
        id = 1L,
        name = "Cupcake",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/pGM4sjt_BdQ",
        price = 299
    ),
    Entity.TempImage(
        id = 2L,
        name = "Donut",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/Yc5sL-ejk6U",
        price = 299
    ),
    Entity.TempImage(
        id = 3L,
        name = "Eclair",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/-LojFX9NfPY",
        price = 299
    ),
    Entity.TempImage(
        id = 4L,
        name = "Froyo",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/3U2V5WqK1PQ",
        price = 299
    ),
    Entity.TempImage(
        id = 5L,
        name = "Gingerbread",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/Y4YR9OjdIMk",
        price = 499
    ),
    Entity.TempImage(
        id = 6L,
        name = "Honeycomb",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/bELvIg_KZGU",
        price = 299
    ),
    Entity.TempImage(
        id = 7L,
        name = "Ice Cream Sandwich",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/YgYJsFDd4AU",
        price = 1299
    ),
    Entity.TempImage(
        id = 8L,
        name = "Jellybean",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/0u_vbeOkMpk",
        price = 299
    ),
    Entity.TempImage(
        id = 9L,
        name = "KitKat",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/yb16pT5F_jE",
        price = 549
    ),
    Entity.TempImage(
        id = 10L,
        name = "Lollipop",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/AHF_ZktTL6Q",
        price = 299
    ),
    Entity.TempImage(
        id = 11L,
        name = "Marshmallow",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/rqFm0IgMVYY",
        price = 299
    ),
    Entity.TempImage(
        id = 12L,
        name = "Nougat",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/qRE_OpbVPR8",
        price = 299
    ),
    Entity.TempImage(
        id = 13L,
        name = "Oreo",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/33fWPnyN6tU",
        price = 299
    ),
    Entity.TempImage(
        id = 14L,
        name = "Pie",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/aX_ljOOyWJY",
        price = 299
    ),
    Entity.TempImage(
        id = 15L,
        name = "Chips",
        imageUrl = "https://source.unsplash.com/UsSdMZ78Q3E",
        price = 299
    ),
    Entity.TempImage(
        id = 16L,
        name = "Pretzels",
        imageUrl = "https://source.unsplash.com/7meCnGCJ5Ms",
        price = 299
    ),
    Entity.TempImage(
        id = 17L,
        name = "Smoothies",
        imageUrl = "https://source.unsplash.com/m741tj4Cz7M",
        price = 299
    ),
    Entity.TempImage(
        id = 18L,
        name = "Popcorn",
        imageUrl = "https://source.unsplash.com/iuwMdNq0-s4",
        price = 299
    ),
    Entity.TempImage(
        id = 19L,
        name = "Almonds",
        imageUrl = "https://source.unsplash.com/qgWWQU1SzqM",
        price = 299
    ),
    Entity.TempImage(
        id = 20L,
        name = "Cheese",
        imageUrl = "https://source.unsplash.com/9MzCd76xLGk",
        price = 299
    ),
    Entity.TempImage(
        id = 21L,
        name = "Apples",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/1d9xXWMtQzQ",
        price = 299
    ),
    Entity.TempImage(
        id = 22L,
        name = "Apple sauce",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/wZxpOw84QTU",
        price = 299
    ),
    Entity.TempImage(
        id = 23L,
        name = "Apple chips",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/okzeRxm_GPo",
        price = 299
    ),
    Entity.TempImage(
        id = 24L,
        name = "Apple juice",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/l7imGdupuhU",
        price = 299
    ),
    Entity.TempImage(
        id = 25L,
        name = "Apple pie",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/bkXzABDt08Q",
        price = 299
    ),
    Entity.TempImage(
        id = 26L,
        name = "Grapes",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/y2MeW00BdBo",
        price = 299
    ),
    Entity.TempImage(
        id = 27L,
        name = "Kiwi",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/1oMGgHn-M8k",
        price = 299
    ),
    Entity.TempImage(
        id = 28L,
        name = "Mango",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/TIGDsyy0TK4",
        price = 299
    )
)