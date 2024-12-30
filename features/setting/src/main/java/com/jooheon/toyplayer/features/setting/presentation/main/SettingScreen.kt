package com.jooheon.toyplayer.features.setting.presentation.main

import android.content.Context
import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.common.compose.components.CustomDivider
import com.jooheon.toyplayer.features.setting.presentation.components.SkipDurationDialog
import com.jooheon.toyplayer.features.setting.model.SettingScreenEvent
import com.jooheon.toyplayer.features.setting.model.SettingScreenItem
import com.jooheon.toyplayer.features.setting.model.SettingScreenState
import com.jooheon.toyplayer.features.setting.presentation.SettingViewModel
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.common.extension.collectAsStateWithLifecycle
import com.jooheon.toyplayer.features.common.extension.sharedViewModel
import com.jooheon.toyplayer.features.setting.R
import com.jooheon.toyplayer.core.strings.UiText

@Composable
fun SettingScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
) {
    val settingViewModel = backStackEntry.sharedViewModel<SettingViewModel>(
        navController = navController,
        parentRoute = null
    ).apply {
        navigateTo.observeWithLifecycle {
            SettingScreenEvent.navigateTo(navController, it)
        }
    }
    val state by settingViewModel.sharedState.collectAsStateWithLifecycle()

    SettingScreen(
        state = state,
        onEvent = settingViewModel::dispatch
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingScreen(
    state: SettingScreenState,
    onEvent: (Context, SettingScreenEvent) -> Unit,
) {
    val context = LocalContext.current
    val settingList = SettingScreenItem.getSettingListItems(state)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if(state.showSkipDurationDialog) {
            SkipDurationDialog(
                currentState = state.skipDuration,
                onChanged = {
                    onEvent(context, SettingScreenEvent.OnSkipDurationChanged(it))
                },
                onDismiss = {
                    onEvent(context, SettingScreenEvent.OnSkipDurationScreenClick(isShow = false))
                }
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
                    IconButton(onClick = { onEvent(context, SettingScreenEvent.OnBackClick) }) {
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

            CustomDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                thickness = 1.dp
            )
            settingList.forEach {
                SettingListItem(
                    item = it,
                    onClick = { onEvent(context, it.event) }
                )
                CustomDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
private fun SettingListItem(
    item: SettingScreenItem,
    onClick: (SettingScreenItem) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick(item) }
    ) {
        Icon(
            painter = rememberVectorPainter(item.iconImageVector),
            contentDescription = item.title.asString(),
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
                text = item.title.asString(),
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
            if (item.showValue) {
                Text(
                    text = item.value.asString(),
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
    ToyPlayerTheme {
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
private fun PreviewSettingScreen() {
    ToyPlayerTheme {
        SettingScreen(
            state = SettingScreenState.default,
            onEvent = { _, _ -> }
        )
    }
}