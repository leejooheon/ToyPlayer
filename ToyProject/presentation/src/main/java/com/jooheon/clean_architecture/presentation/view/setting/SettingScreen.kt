package com.jooheon.clean_architecture.presentation.view.setting


import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlaylistUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicPlayerViewModel
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.UiText
import com.jooheon.clean_architecture.presentation.view.components.MyDivider
import com.jooheon.clean_architecture.presentation.view.main.MainViewModel
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel

import com.jooheon.clean_architecture.presentation.view.temp.EmptyMusicUseCase
import com.jooheon.clean_architecture.presentation.view.temp.EmptySettingUseCase
import com.jooheon.clean_architecture.presentation.view.temp.EmptySubwayUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navigator: NavController,
    viewModel: SettingViewModel = hiltViewModel(sharedViewModel()),
    mainViewModel: MainViewModel = hiltViewModel(sharedViewModel()),
) {
    val context = LocalContext.current
    val skipDuration = viewModel.skipState.collectAsState()
    val settingList = viewModel.getSettingList(context)
    var dialogState by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if(dialogState) {
            SkipDurationDialog(
                currentState = skipDuration.value,
                onChanged = {
                    viewModel.onSkipItemClick(it)
                    mainViewModel.musicPlayerViewModel.onSkipDurationChanged()
                },
                onDismiss = { dialogState = false }
            )
        }

        Column(
            modifier = Modifier.statusBarsPadding()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = UiText.StringResource(R.string.title_settings).asString(),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            MyDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                thickness = 1.dp
            )
            settingList.forEach {
                SettingListItem(
                    data = it,
                    onClick = viewModel::onSettingItemClick
                )
                MyDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    thickness = 1.dp
                )
            }
        }
    }
    ObserveEvents(
        navigator = navigator,
        viewModel = viewModel,
        onDurationEvent = {
            dialogState = true
        },
        onEqualizerEvent = {
            val sessionId = mainViewModel.musicPlayerViewModel.audioSessionId()
            viewModel.onEqualizerClick(context, sessionId)
        }
    )
}

@Composable
private fun SettingListItem(
    data: SettingData,
    onClick: (SettingData) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick(data) }
    ) {
        Icon(
            painter = rememberVectorPainter(data.iconImageVector),
            contentDescription = data.title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .weight(0.12f)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(0.64f)
        ) {
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .padding(end = 12.dp)
                .weight(0.24f)
        ) {
            if (data.showValue) {
                Text(
                    text = data.value,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ObserveEvents(
    navigator: NavController,
    viewModel: SettingViewModel,
    onDurationEvent: () -> Unit,
    onEqualizerEvent: () -> Unit
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    val context = LocalContext.current
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { lifecycleOwner, event ->

                lifecycleOwner.lifecycleScope.launch {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.navigateToSettingDetailScreen.collectLatest {
                            when(it.action) {
                                SettingAction.LAUGUAGE,
                                SettingAction.THEME-> {
                                    viewModel.parseRoute(it.action)?.let {
                                        navigator.navigate(it)
                                    }
                                }
                                SettingAction.EQUALIZER -> {
                                    onEqualizerEvent()
                                }
                                SettingAction.SKIP_DURATION -> {
                                    onDurationEvent()
                                }
                            }
                        }
                    }
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingDetailItem(
    @ColorRes color: Color,
    selected: Boolean,
    title: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.padding(8.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title
            )
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}

@Composable
@Preview
private fun PreviewSettingDetailItem() {
    PreviewTheme(false) {
        Column {
            SettingDetailItem(
                color = MaterialTheme.colorScheme.primary,
                selected = true,
                title = "ITEM - 1 selected",
                modifier = Modifier.fillMaxWidth(),
                onClick = { }
            )
            SettingDetailItem(
                color = MaterialTheme.colorScheme.background,
                selected = false,
                title = "ITEM - 2",
                modifier = Modifier.fillMaxWidth(),
                onClick = { }
            )
        }
    }
}
@Composable
@Preview
private fun PreviewSettingListItem() {
    val context = LocalContext.current
    val data = SettingViewModel(EmptySettingUseCase()).getSettingList(
        context = context
    )
    PreviewTheme(false) {
        SettingListItem(
            data = data.last(),
            onClick = {}
        )
    }
}

@Composable
@Preview
private fun PreviewSettingScreen() {
    val context = LocalContext.current
    val scope = CoroutineScope(Dispatchers.Main)

    val musicPlaylistUseCase = MusicPlaylistUseCase(EmptyMusicUseCase())

    val musicPlayerViewModel = MusicPlayerViewModel(
        context = context,
        applicationScope = scope,
        musicController = MusicController(
            context = context, 
            applicationScope = scope,
            musicPlaylistUseCase = musicPlaylistUseCase,
            settingUseCase = EmptySettingUseCase(), 
            isPreview = true
        )
    )
    val viewModel = MainViewModel(EmptySubwayUseCase(), musicPlayerViewModel)
    PreviewTheme(false) {
        SettingScreen(
            navigator = NavController(context),
            viewModel = SettingViewModel(EmptySettingUseCase()),
            mainViewModel = viewModel,
        )
    }
}