package com.jooheon.clean_architecture.presentation.view.main.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsHeight
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.presentation.utils.HandleApiFailure
import com.jooheon.clean_architecture.presentation.utils.ShowLoading
import com.jooheon.clean_architecture.presentation.view.home.repo.RepositoryCollection
import com.jooheon.clean_architecture.presentation.view.main.MainViewModel

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ShowRepositories(mainViewModel)
    }
}

@Composable
fun ShowRepositories(viewModel: MainViewModel) {
    val response = viewModel.repositoryResponse.value

    when(response) {
        is Resource.Loading -> {
            ShowLoading()
        }
        is Resource.Success -> {
            Box(modifier = Modifier) {
                LazyColumn {
                    item {
                        Spacer(Modifier.statusBarsHeight(additional = 28.dp))
                    }
                    item(response.value) {
                        RepositoryCollection(
                            owner = viewModel.lastSearchedOwner.value,
                            repositoryList = response.value) {
                            Log.d(TAG, "onClicked: $it")
                        }
                    }
                }
            }
        }
        is Resource.Failure -> {
            HandleApiFailure(response = response)
        }
        is Resource.Default -> {
            InfoText(text = "Resource.Default")
        }
    }
}

@Composable
fun InfoText(text: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text)
    }
}