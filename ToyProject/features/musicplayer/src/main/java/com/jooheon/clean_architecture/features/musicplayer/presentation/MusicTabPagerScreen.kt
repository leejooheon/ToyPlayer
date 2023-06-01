package com.jooheon.clean_architecture.features.musicplayer.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.common.extension.collectAsStateWithLifecycle
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.pagerTabIndicatorOffset
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.MusicPlayerScreenViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.MusicScreen

import kotlinx.coroutines.launch

@OptIn(
    ExperimentalFoundationApi::class
)
@Composable
fun MusicTabPagerScreen(
    navigator: NavController,
) {
    val tabPages = listOf(
        stringResource(id = R.string.tab_name_song ),
        stringResource(id = R.string.tab_name_album),
        stringResource(id = R.string.tab_name_artist),
        stringResource(id = R.string.tab_name_playlist)
    )

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val scrollToPage: (Int) -> Unit = { page ->
        scope.launch { pagerState.animateScrollToPage(page) }
        Unit
    }

    BackHandler {
        when {
            pagerState.currentPage != 0 -> scrollToPage(0)
            else -> navigator.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSecondary)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .pagerTabIndicatorOffset(
                                pagerState = pagerState,
                                tabPositions = tabPositions
                            )
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                }
            ) {
                tabPages.forEachIndexed { i, title ->
                    val selected = pagerState.currentPage == i

                    Tab(
                        selected = selected,
                        text = {
                            Text(
                                text = title,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if(selected) MaterialTheme.colorScheme.onBackground
                                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        },
                        onClick = {
                            scrollToPage(i)
                        }
                    )
                }
            }

            HorizontalPager(
                pageCount = 4,
                state = pagerState
            ) { page ->

                when (page) {
                    0 -> {
                        val viewModel = hiltViewModel<MusicPlayerScreenViewModel>()
                        val musicPlayerScreenState by viewModel.musicPlayerScreenState.collectAsStateWithLifecycle()

                        MusicScreen(
                            musicPlayerScreenState = musicPlayerScreenState,
                            onEvent = viewModel::dispatch
                        )
                    }
                    1 -> { /** TODO **/}
                    2 -> { /** TODO **/}
                    3 -> { /** TODO **/}
                }
            }
        }

    }
}
@Preview
@Composable
private fun MusicTabPagerScreenPreviewDark() {
    val context = LocalContext.current
    PreviewTheme(true) {
        MusicTabPagerScreen(navigator = NavController(context),)
    }
}