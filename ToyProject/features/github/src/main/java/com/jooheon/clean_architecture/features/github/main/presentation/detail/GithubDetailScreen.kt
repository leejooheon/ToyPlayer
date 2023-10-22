package com.jooheon.clean_architecture.features.github.main.presentation.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.toyproject.features.common.compose.components.CustomDivider
import com.jooheon.clean_architecture.toyproject.features.common.compose.components.CustomSurface
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.CustomTheme
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.github.main.model.GithubDetailScreenState
import com.jooheon.clean_architecture.features.github.main.presentation.main.components.RepositoryImage
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.toyproject.features.github.R
import kotlin.math.max
import kotlin.math.min

private val BottomBarHeight = 56.dp
private val TitleHeight = 128.dp
private val GradientScroll = 180.dp
private val ImageOverlap = 115.dp
private val MinTitleOffset = 56.dp
private val MinImageOffset = 12.dp
private val MaxTitleOffset = ImageOverlap + MinTitleOffset + GradientScroll
private val ExpandedImageSize = 300.dp
private val CollapsedImageSize = 150.dp
private val HzPadding = Modifier.padding(horizontal = 24.dp)

@Composable
fun GithubDetailScreen(
    githubId: String,
    repository: Entity.Repository,
    viewModel: GithubDetailScreenViewModel = hiltViewModel()
) {
    viewModel.initState(
        id = githubId,
        item = repository
    )

    GithubDetailScreen(
        state = viewModel.state
    )
}

@Composable
private fun GithubDetailScreen(
    state: GithubDetailScreenState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val scroll = rememberScrollState(0)
        Header()
        Body(state, scroll)
        Title(state.item.name, state.item.created_at, scroll.value)
        Image(state.item.imageUrl, scroll.value)
    }
}

@Composable
private fun Header() {
    Spacer(
        modifier = Modifier
            .height(280.dp)
            .fillMaxWidth()
            .background(Brush.horizontalGradient(CustomTheme.colors.gradient2_1))
    )
}

@Composable
private fun Image(
    imageUrl: String,
    scroll: Int
) {
    val collapseRange = with(LocalDensity.current) {
        (MaxTitleOffset - MinTitleOffset).toPx()
    }
    val collapseFraction = (scroll / collapseRange).coerceIn(0f, 1f)

    CollapsingImageLayout(
        collapseFraction = collapseFraction,
        modifier = HzPadding.then(Modifier.statusBarsPadding())
    ) {
        RepositoryImage(
            imageUrl = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun Title(
    name: String,
    date: String,
    scroll: Int
) {
    val maxOffset = with(LocalDensity.current) {
        MaxTitleOffset.toPx()
    }
    val minOffset = with(LocalDensity.current) {
        MinTitleOffset.toPx()
    }
    val offset = (maxOffset - scroll).coerceAtLeast(minOffset)

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .heightIn(min = TitleHeight)
            .statusBarsPadding()
            .graphicsLayer { translationY = offset }
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = HzPadding
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = date,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = HzPadding
        )
        Spacer(Modifier.height(8.dp))
        CustomDivider()
    }
}

@Composable
private fun CollapsingImageLayout(
    collapseFraction: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        check(measurables.size == 1)

        val imageMaxSize = min(ExpandedImageSize.roundToPx(), constraints.maxWidth)
        val imageMinSize = max(CollapsedImageSize.roundToPx(), constraints.minWidth)
        val imageWidth = lerp(imageMaxSize, imageMinSize, collapseFraction)
        val imagePlaceable = measurables[0].measure(Constraints.fixed(imageWidth, imageWidth))

        val imageY = lerp(MinTitleOffset, MinImageOffset, collapseFraction).roundToPx()
        val imageX = lerp(
            (constraints.maxWidth - imageWidth) / 2, // centered when expanded
            constraints.maxWidth - imageWidth, // right aligned when collapsed
            collapseFraction
        )
        layout(
            width = constraints.maxWidth,
            height = imageY + imageWidth
        ) {
            imagePlaceable.placeRelative(imageX, imageY)
        }
    }
}

@Composable
private fun Body(
    state: GithubDetailScreenState,
    scroll: ScrollState
) {
    val detailContent = branchReComposableHandler(state.branchList)
    val detailCommit  = commitReComposableHandler(state.commitList)
    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(MinTitleOffset)
        )
        Column(
            modifier = Modifier
                .verticalScroll(scroll)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Spacer(Modifier.height(GradientScroll))
            CustomSurface(Modifier.fillMaxWidth()) {
                Column {
                    Spacer(Modifier.height(ImageOverlap))
                    Spacer(Modifier.height(TitleHeight))

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = UiText.StringResource(R.string.branch_header).asString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = HzPadding
                    )
                    Spacer(Modifier.height(16.dp))

                    val seeMore = remember { mutableStateOf(true)}

                    Text(
                        text = detailContent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (seeMore.value) 5 else Int.MAX_VALUE,
                        overflow = TextOverflow.Ellipsis,
                        modifier = HzPadding
                    )

                    val textButton = if (seeMore.value) {
                        UiText.StringResource(R.string.see_more)
                    } else {
                        UiText.StringResource(R.string.see_less)
                    }

                    Text(
                        text = textButton.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .heightIn(20.dp)
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                            .clickable {
                                seeMore.value = !seeMore.value
                            }
                    )

                    Spacer(Modifier.height(40.dp))

                    Text(
                        text = UiText.StringResource(R.string.commit_header).asString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = HzPadding
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = detailCommit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = HzPadding
                    )

                    Spacer(Modifier.height(4.dp))
                    CustomDivider()

                    Spacer(
                        modifier = Modifier
                            .padding(bottom = BottomBarHeight)
                            .navigationBarsPadding(start = false, end = false)
                            .height(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun branchReComposableHandler(items: List<Entity.Branch>): String {
    var stringBuilder = ""

    items.forEachIndexed { index, branch ->
        stringBuilder += "$index: ${branch.name}\n${branch.commit.url}\n${branch.commit.sha}\n"
    }

    val result = if(stringBuilder.isEmpty()) {
        UiText.StringResource(R.string.branch_placeholder).asString()
    } else {
        stringBuilder
    }

    return result
}

@Composable
private fun commitReComposableHandler(items: List<Entity.Commit>): String {
    var stringBuilder = ""

    items.forEachIndexed { index, data ->
        stringBuilder += "$index: ${data.commit.message}\n"
    }

    val result = if(stringBuilder.isEmpty()) {
        UiText.StringResource(R.string.commit_placeholder).asString()
    } else {
        stringBuilder
    }

    return result
}

@Preview
@Composable
fun RepositoryDetailScreenPreview() {
    val item: Entity.Repository = Entity.Repository(
        name = "name",
        id = "id",
        created_at = "created_at",
        html_url = "https://asd.com",
        imageUrl = "image"
    )

    val name = remember(item) { item.name }
    val date = remember(item) { item.created_at}
    PreviewTheme(false) {
        Box(Modifier.fillMaxSize()) {
            val scroll = rememberScrollState(0)
            Header()
            Body(GithubDetailScreenState.default, scroll)
            Title(name, date, scroll.value)
            Image(item.imageUrl, scroll.value)
        }
    }
}