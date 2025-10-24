package com.jooheon.toyplayer.features.dlna

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.dlna.DlnaConnectionState
import com.jooheon.toyplayer.features.commonui.components.CustomTopAppBar
import com.jooheon.toyplayer.features.dlna.model.DlnaAction
import com.jooheon.toyplayer.features.dlna.model.DlnaUiState

@Composable
fun DlnaScreen(
    onBack: () -> Unit,
    viewModel: DlnaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DlnaScreenInternal(
        uiState = uiState,
        onBackClick = onBack,
        onAction = viewModel::dispatch
    )
}

@Composable
private fun DlnaScreenInternal(
    uiState: DlnaUiState,
    onAction: (DlnaAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = UiText.StringResource(Strings.title_dlna).asString(),
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onAction(DlnaAction.OnConnect(context)) },
                            enabled = uiState.connectionState == DlnaConnectionState.Idle
                        ) {
                            Text("서비스 연결")
                        }
                        Button(
                            onClick = { onAction(DlnaAction.OnDiscover(context)) },
                            enabled = uiState.connectionState != DlnaConnectionState.Idle
                        ) {
                            Text("기기 검색")
                        }
                        Button(
                            onClick = { onAction(DlnaAction.OnDisconnect(context)) },
                            enabled = uiState.connectionState == DlnaConnectionState.Connected
                        ) {
                            Text("연결 해제")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "State: ${uiState.state}",
                        modifier = Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.medium
                        )
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "DLNA 기기 목록",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(8.dp),
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(uiState.rendererList) { renderer ->
                            val isSelected = renderer.udn == uiState.selectedRenderer.udn
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        val action = if (isSelected) {
                                            DlnaAction.OnDisconnect(context)
                                        } else {
                                            DlnaAction.OnConnect(context)
                                        }
                                        onAction(action)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            renderer.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            renderer.udn,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    if (isSelected) {
                                        when (uiState.connectionState) {
                                            DlnaConnectionState.Connecting -> CircularProgressIndicator()
                                            DlnaConnectionState.Connected -> {
                                                Text(
                                                    "연결됨",
                                                    color = MaterialTheme.colorScheme.primary,
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }

                                            DlnaConnectionState.Failed -> {
                                                Text(
                                                    "끊김",
                                                    color = MaterialTheme.colorScheme.primary,
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }

                                            else -> { /* no-op */
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
//        when {
//            uiState.rendererList.isEmpty() -> {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text("검색된 기기가 없습니다.")
//                    Spacer(Modifier.height(8.dp))
//                    Button(onClick = {
//                        val action = DlnaAction.OnDiscover(context)
//                        onAction(action)
//                    }) {
//                        Text("새로고침")
//                    }
//                }
//            }
//            else -> {
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("State: ${uiState.state}")
//
//                    Text(
//                        "DLNA 기기 목록",
//                        style = MaterialTheme.typography.titleMedium,
//                        modifier = Modifier.padding(bottom = 12.dp)
//                    )
//
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .weight(1f)
//                    ) {
//                        items(uiState.rendererList) { renderer ->
//                            val isSelected = renderer.udn == uiState.selectedRenderer.udn
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 4.dp)
//                                    .clickable {
//                                        val action = if (isSelected && uiState.connectionState == DlnaConnectionState.Ready)
//                                            DlnaAction.OnDisconnect(context) else DlnaAction.OnConnect(context)
//                                        onAction(action)
//                                    },
//                                colors = CardDefaults.cardColors(
//                                    containerColor = if (isSelected)
//                                        MaterialTheme.colorScheme.primaryContainer
//                                    else
//                                        MaterialTheme.colorScheme.surface
//                                ),
//                                elevation = CardDefaults.cardElevation(2.dp)
//                            ) {
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(12.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Column(modifier = Modifier.weight(1f)) {
//                                        Text(renderer.name, style = MaterialTheme.typography.bodyLarge)
//                                        Text(renderer.udn, style = MaterialTheme.typography.bodySmall)
//                                    }
//
//                                    if (isSelected && uiState.connectionState == DlnaConnectionState.Ready) {
//                                        Text(
//                                            "연결됨",
//                                            color = MaterialTheme.colorScheme.primary,
//                                            style = MaterialTheme.typography.labelMedium
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    Spacer(Modifier.height(16.dp))
//
//                    when (uiState.connectionState) {
//                        DlnaConnectionState.Ready -> {
//                            Button(onClick = {
//                                onAction(DlnaAction.OnDisconnect(context))
//                            }) {
//                                Text("연결 해제")
//                            }
//                        }
//                        DlnaConnectionState.Connecting -> {
//                            CircularProgressIndicator()
//                        }
//                        else -> {
//                            Button(onClick = {
//                                onAction(DlnaAction.OnDiscover(context))
//                            }) { Text("기기 검색") }
//                        }
//                    }
//                }
//            }
//        }
}


@Preview
@Composable
private fun PreviewDlnaScreen() {
    ToyPlayerTheme {
        DlnaScreenInternal(
            uiState = DlnaUiState.default,
            onAction = { },
            onBackClick = { }
        )
    }
}