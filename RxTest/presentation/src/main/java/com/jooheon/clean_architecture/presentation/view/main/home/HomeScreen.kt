package com.jooheon.clean_architecture.presentation.view.main.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsHeight
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.presentation.theme.CustomTheme
import com.jooheon.clean_architecture.presentation.theme.ProvideCustomColors
import com.jooheon.clean_architecture.presentation.utils.HandleApiFailure
import com.jooheon.clean_architecture.presentation.utils.ShowLoading
import com.jooheon.clean_architecture.presentation.view.components.MyDivider
import com.jooheon.clean_architecture.presentation.view.custom.GithubSearchDialog
import com.jooheon.clean_architecture.presentation.view.destinations.RepositoryDetailScreenDestination
import com.jooheon.clean_architecture.presentation.view.home.repo.RepositoryCollection
import com.jooheon.clean_architecture.presentation.view.main.MainViewModel
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptyGithubUseCase
import com.jooheon.clean_architecture.presentation.view.temp.PreviewPallete
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel = hiltViewModel(),
    sharedViewModel: MainViewModel = hiltViewModel(sharedViewModel())
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            item {
                ShowRepositories(sharedViewModel, homeViewModel, navigator)
            }
        }
    }
}

@Composable
fun ShowRepositories(mainViewModel: MainViewModel, homeViewModel: HomeViewModel, navigator:DestinationsNavigator) {
    val response = mainViewModel.repositoryResponse.value
    val scope = rememberCoroutineScope()

    if(response is Resource.Loading) {
        Spacer(Modifier.statusBarsHeight(additional = 16.dp))
        ShowLoading()
        return
    }

    when(response) {
        is Resource.Success -> {
            Spacer(Modifier.statusBarsHeight(additional = 12.dp))
            RepositoryCollection(
                owner = mainViewModel.lastSearchedOwner.value,
                repositoryList = response.value) {
                Log.d(TAG, "onClicked: $it")
                mainViewModel.callCommitAndBranchApi(it.name)

                navigator.navigate(RepositoryDetailScreenDestination(it)) {
                    launchSingleTop = true
                }
            }
            Spacer(Modifier.statusBarsHeight(additional = 12.dp))
            MyDivider(thickness = 2.dp)
        }
        is Resource.Failure -> {
            HandleApiFailure(response = response)
        }
        is Resource.Default -> {
            InfoText(mainViewModel, "Input your id !!")
        }
        else -> {
        }
    }
}

@Composable
fun ShowBranches(
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel
) {
    if(!(mainViewModel.repositoryResponse.value is Resource.Success)) {
        return
    }
}

@Composable
fun InfoText(
    mainViewModel: MainViewModel,
    text: String
) {
    val openGithubSearchDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.uiBackground)
            .wrapContentSize(Alignment.Center),
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = CustomTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            modifier = Modifier
                .padding(16.dp)
                .clickable { openGithubSearchDialog.value = true }
        )
    }

    if(openGithubSearchDialog.value) {
        GithubSearchDialog(openGithubSearchDialog, onDismiss = { owner ->
            if (!owner.isEmpty()) {
                Log.d(TAG, owner)
                mainViewModel.callRepositoryApi(owner)
            }
        })
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val mainViewModel = MainViewModel(EmptyGithubUseCase())
    val homeViewModel = HomeViewModel(EmptyGithubUseCase())
    ProvideCustomColors(PreviewPallete) {
        HomeScreen(EmptyDestinationsNavigator, homeViewModel, mainViewModel)
    }
}