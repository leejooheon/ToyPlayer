package com.jooheon.clean_architecture.presentation.view.main.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
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
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.theme.CustomTheme
import com.jooheon.clean_architecture.presentation.view.custom.CustomSurface
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.theme.ProvideCustomColors
import com.jooheon.clean_architecture.presentation.utils.ShowLoading
import com.jooheon.clean_architecture.presentation.view.components.MyDivider
import com.jooheon.clean_architecture.presentation.view.custom.RepositoryImage
import com.jooheon.clean_architecture.presentation.view.main.MainViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptyGithubUseCase
import com.jooheon.clean_architecture.presentation.view.temp.PreviewPallete
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel

import com.ramcosta.composedestinations.annotation.Destination
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

@Destination
@Composable
fun RepositoryDetailScreen(
    item: Entity.Repository,
    sharedViewModel: MainViewModel = hiltViewModel(sharedViewModel()),
) {
    val name = remember(item) { item.name }
    val date = remember(item) { item.created_at}

    Box(Modifier.fillMaxSize()) {
        val scroll = rememberScrollState(0)
        Header()
        Body(sharedViewModel, scroll)
        Title(name, date, scroll.value)
        Image(item.imageUrl, scroll.value)
    }
}

@Composable
private fun Header() {
    Spacer(
        modifier = Modifier
            .height(280.dp)
            .fillMaxWidth()
            .background(Brush.horizontalGradient(CustomTheme.colors.tornado1))
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
            .background(color = CustomTheme.colors.uiBackground)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.h4,
            color = CustomTheme.colors.textSecondary,
            modifier = HzPadding
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = date,
            style = MaterialTheme.typography.subtitle2,
            color = CustomTheme.colors.textHelp,
            modifier = HzPadding
        )
        Spacer(Modifier.height(8.dp))
        MyDivider()
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
    viewModel: MainViewModel,
    scroll: ScrollState
) {
    val detailContent = branchReComposableHandler(viewModel)
    val detailCommit  = commitReComposableHandler(viewModel)
    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(MinTitleOffset)
        )
        Column(
            modifier = Modifier.verticalScroll(scroll)
        ) {
            Spacer(Modifier.height(GradientScroll))
            CustomSurface(Modifier.fillMaxWidth()) {
                Column {
                    Spacer(Modifier.height(ImageOverlap))
                    Spacer(Modifier.height(TitleHeight))

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.branch_header),
                        style = MaterialTheme.typography.overline,
                        color = CustomTheme.colors.textHelp,
                        modifier = HzPadding
                    )
                    Spacer(Modifier.height(16.dp))

                    val seeMore = remember { mutableStateOf(true)}

                    Text(
                        text = detailContent,
                        style = MaterialTheme.typography.body1,
                        color = CustomTheme.colors.textHelp,
                        maxLines = if (seeMore.value) 5 else Int.MAX_VALUE,
                        overflow = TextOverflow.Ellipsis,
                        modifier = HzPadding
                    )
                    
                    val textButton = if (seeMore.value) {
                        stringResource(R.string.see_more)
                    } else {
                        stringResource(R.string.see_less)
                    }

                    Text(
                        text = textButton,
                        style = MaterialTheme.typography.button,
                        textAlign = TextAlign.Center,
                        color = CustomTheme.colors.textLink,
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
                        text = stringResource(R.string.commit_header),
                        style = MaterialTheme.typography.overline,
                        color = CustomTheme.colors.textHelp,
                        modifier = HzPadding
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = detailCommit,
                        style = MaterialTheme.typography.body1,
                        color = CustomTheme.colors.textHelp,
                        modifier = HzPadding
                    )

                    Spacer(Modifier.height(4.dp))
                    MyDivider()

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
private fun branchReComposableHandler(viewModel: MainViewModel): String {
    val result: String
    val response = viewModel.branchResponse.value

    when(response) {
        is Resource.Success -> {
            var stringBuilder = ""
            response.value.forEachIndexed { index, branch ->
                stringBuilder += "$index: ${branch.name}\n${branch.commit.url}\n${branch.commit.sha}\n"
            }
            result = stringBuilder
        }
        is Resource.Loading -> {
            ShowLoading()
            result = stringResource(R.string.branch_placeholder)
        }
        else -> {
            result = stringResource(R.string.branch_placeholder)
        }
    }
    return result
}

@Composable
private fun commitReComposableHandler(viewModel: MainViewModel): String {
    val result: String
    val response = viewModel.commitResponse.value

    when(response) {
        is Resource.Success -> {
            var stringBuilder = ""
            response.value.forEachIndexed { index, data ->
                stringBuilder += "$index: ${data.commit.message}\n"
            }
            result = stringBuilder
        }
        is Resource.Loading -> {
            ShowLoading()
            result = stringResource(R.string.commit_placeholder)
        }
        else -> {
            result = stringResource(R.string.commit_placeholder)
        }
    }
    return result
}

@Preview
@Composable
fun RepositoryDetailScreenPreview() {
    val viewModel = MainViewModel(EmptyGithubUseCase())
    val item: Entity.Repository = Entity.Repository(
        name = "name",
        id = "id",
        created_at = "created_at",
        html_url = "https://asd.com",
        imageUrl = "image"
    )

    val name = remember(item) { item.name }
    val date = remember(item) { item.created_at}
    ProvideCustomColors(colors = PreviewPallete) {
        Box(Modifier.fillMaxSize()) {
            val scroll = rememberScrollState(0)
            Header()
            Body(viewModel, scroll)
            Title(name, date, scroll.value)
            Image(item.imageUrl, scroll.value)
        }
    }
}