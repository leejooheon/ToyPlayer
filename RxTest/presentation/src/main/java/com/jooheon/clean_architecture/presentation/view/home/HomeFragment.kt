package com.jooheon.clean_architecture.presentation.view.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.base.BaseComposeFragment
import com.jooheon.clean_architecture.presentation.view.custom.GithubRepositoryCard
import com.jooheon.clean_architecture.presentation.view.custom.GithubSearchDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseComposeFragment() {
    private val TAG = HomeFragment::class.java.simpleName

    private val viewModel: HomeViewModel by viewModels()

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TopBar()
            }
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Preview
    @Composable
    fun TopBar() {
        Scaffold(
            isFloatingActionButtonDocked = false,
            topBar = {
                TopAppBar(
                    title = { Text(text = "My ToyProject") },
                    backgroundColor = Color.White,
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.onNavigationClicked()
                            Log.d(TAG, "navigation")
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        val openDialog = remember { mutableStateOf(false) }
                        if(openDialog.value) {
                            GithubSearchDialog(openDialog = openDialog, onDismiss = { owner ->
                                if (!owner.isEmpty()) {
                                    viewModel.callRepositoryApi(owner)
                                }
                            })
                        }

                        IconButton(onClick = {
                            viewModel.onFavoriteClicked()
                        }) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "first IconButton description")
                        }
                        IconButton(onClick = {
                            openDialog.value = true
                        }) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "second IconButton description")
                        }
                        IconButton(onClick = {
                            viewModel.onSettingClicked()
                            Log.d(TAG, "third IconButton")
                        }) {
                            Icon(Icons.Filled.Settings, contentDescription = null)
                        }
                    }
                )
            },
            drawerContent = {

            }
        ) {
            // screen conetnt
            HelloText()
            LoadingHandler()
        }
    }

    @Composable
    fun LoadingHandler() {
        viewModel.loadingResponse.value
        if(viewModel.loadingResponse.value) {
            showLoading()
        } else {
            hideLoading()
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Composable
    fun HelloText() {
        val response = viewModel.repositoryResponse.value

        when(response) {
            is Resource.Success -> {
                DrawRepositories(response.value)
            }
            is Resource.Failure -> {
                handleApiFailure(response = response)
            }
            is Resource.Default -> {
                InfoText(text = "Resource.Default")
            }
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Composable
    fun DrawRepositories(repositories: List<Entity.Repository>) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            cells = GridCells.Fixed(2)
        ) {
            itemsIndexed(repositories) { index, repository ->
                val modifier = Modifier
                    .fillMaxWidth(0.5f) // half width
                    .padding(16.dp)
                GithubRepositoryCard(
                    modifier,
                    repository,
                    onItemClicked = {
//                        viewModel.callCommitApi(it.name)
                                    viewModel.multipleApiTest(it.name)
                    },
                    onInfoButtonClicked = {

                    }
                )
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
}