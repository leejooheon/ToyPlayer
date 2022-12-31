package com.jooheon.clean_architecture.presentation.view.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navigator: NavController,
    viewModel: SettingViewModel = hiltViewModel(),
    isPreview: Boolean = false
) {

    val settingString = if(!isPreview) {
        stringResource(id = R.string.setting)
    } else {
        "Setting"
    }

    val settingList = SettingViewModel.dummyData
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = settingString,
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

            settingList.forEach {
                SettingListItem(
                    data = it,
                    onClick = viewModel::onSettingItemClick
                )
            }
        }
    }
    ObserveEvents(
        navigator = navigator,
        viewModel = viewModel
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
            modifier = Modifier
                .size(24.dp)
                .weight(0.12f)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(0.68f)
        ) {
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            )
        }

        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .padding(end = 12.dp)
                .weight(0.2f)
        ) {
            if (data.showValue) {
                Text(
                    text = data.value,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}


@Composable
private fun ObserveEvents(
    navigator: NavController,
    viewModel: SettingViewModel
) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { lifecycleOwner, event ->
                lifecycleOwner.lifecycleScope.launch {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.navigateToSettingDetailScreen.collectLatest {
//                            navigator.navigate(it)
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

@Composable
@Preview
private fun PreviewSettingListItem() {
    val data = SettingViewModel.dummyData.last()
    PreviewTheme(false) {
        SettingListItem(
            data = data,
            onClick = {}
        )
    }
}

@Composable
@Preview
private fun PreviewSettingScreen() {
    val context = LocalContext.current
    PreviewTheme(false) {
        SettingScreen(
            navigator = NavController(context),
            isPreview = true
        )
    }
}