package com.jooheon.clean_architecture.presentation.view.main.github

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsHeight
import com.jooheon.clean_architecture.presentation.theme.themes.CustomTheme
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.ObserveAlertDialogState
import com.jooheon.clean_architecture.presentation.utils.ObserveLoadingState
import com.jooheon.clean_architecture.presentation.view.components.MyDivider
import com.jooheon.clean_architecture.presentation.view.components.outlinedTextFieldColor
import com.jooheon.clean_architecture.presentation.view.destinations.RepositoryDetailScreenDestination
import com.jooheon.clean_architecture.presentation.view.home.repo.GithubRepositoryItem
import com.jooheon.clean_architecture.presentation.view.temp.EmptyGithubUseCase
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator


private const val TAG = "HomeScreen"

@ExperimentalComposeUiApi
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    githubViewModel: GithubViewModel = hiltViewModel(),
    isPreview: Boolean = false
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchView(githubViewModel)
        RepositoryItems(githubViewModel, navigator, isPreview)
    }
    ObserveAlertDialogState(githubViewModel)
    ObserveLoadingState(githubViewModel)
}


@ExperimentalComposeUiApi
@Composable
private fun SearchView(
    viewModel: GithubViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val maxCharacterSize = 10
        var text by remember { mutableStateOf(viewModel.githubId.value) }
        val keyboardController = LocalSoftwareKeyboardController.current

        Text(
            modifier = Modifier.padding(10.dp),
            text = "Input your\ngithub id",
            color = CustomTheme.colors.textPrimary,
            textAlign = TextAlign.Start,

            style = MaterialTheme.typography.h5,
            overflow = TextOverflow.Ellipsis
        )

        OutlinedTextField(
            modifier = Modifier.width(150.dp),
            value = text,
            onValueChange = {
                if(it.length <= maxCharacterSize) {
                    viewModel.githubId.value = it
                    text = it
                }
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            label = {
                Text(
                    text = "Input",
                    color = CustomTheme.colors.textSecondary
                )
            },
            placeholder = {
                Text(
                    text = "input keyword",
                    style = TextStyle(
                        color = CustomTheme.colors.textHelp,
                        textAlign = TextAlign.Center
                    )
                )
            },
            colors = outlinedTextFieldColor(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )

        OutlinedButton(
            modifier = Modifier
                .padding(start = 10.dp),
            onClick = {
                Log.d(TAG, "onClick!!")
                keyboardController?.hide()
                viewModel.callRepositoryApi()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = CustomTheme.colors.uiFloated
            )
        ) {
            Text(
                text = "확인",
                color = CustomTheme.colors.textHelp
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState", "StateFlowValueCalledInComposition")
@Composable
fun RepositoryItems(
    viewModel: GithubViewModel,
    navigator:DestinationsNavigator,
    isPreview:Boolean = false
) {
    val githubId = viewModel.githubId.value // FIXME: id는 다른데서 참조해야할듯
    val response = viewModel.repositoryResponse.value

    Spacer(Modifier.statusBarsHeight(additional = 12.dp))
    MyDivider(thickness = 2.dp)

    response?.let {
        GithubRepositoryItem(
            owner = githubId,
            repositoryList = it) { item ->
            Log.d(TAG, "onClicked: $item")
            navigator.navigate(RepositoryDetailScreenDestination(githubId, item)) {
                launchSingleTop = true
            }
        }
    }

    if(isPreview) {
        GithubRepositoryItem(
            owner = "owner",
            repositoryList = EmptyGithubUseCase.repositoryDummyData(),
            onRepositoryClick = {
                // nothing
            }
        )
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun HomeScreenPreview() {
    val viewModel = GithubViewModel(EmptyGithubUseCase())
    PreviewTheme(true) {
        HomeScreen(EmptyDestinationsNavigator, viewModel, true)
    }
}