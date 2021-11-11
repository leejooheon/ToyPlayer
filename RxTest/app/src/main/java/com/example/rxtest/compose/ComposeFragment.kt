package com.example.rxtest.compose

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rxtest.main.MainActivity

class ComposeFragment: Fragment(){
    private val TAG = ComposeFragment::class.simpleName
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                bottomBar()
            }
        }
    }

    @Composable
    fun HelloText() {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Hello world"
            )
            Text(text = "Hello world222"
            )
        }
    }
    @Preview
    @Composable
    fun bottomBar() {
        Scaffold(
            isFloatingActionButtonDocked = false,
            topBar = {
                TopAppBar(
                    title = {Text(text = "this is topAppBar")},
                    backgroundColor = Color.White,
                    navigationIcon = {
                        IconButton(onClick = {

                            Log.d(TAG, "navigation")
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            Log.d(TAG, "first IconButton")
                        }) {
                            Icon(Icons.Filled.Favorite,
                                contentDescription = "first IconButton description")
                        }
                        IconButton(onClick = {
                            (requireActivity() as? MainActivity)?.viewModel?.replaceRepositoryFragment(requireActivity().supportFragmentManager)
                        }) {
                            Icon(Icons.Filled.Search,
                            contentDescription = "second IconButton description")
                        }
                        IconButton(onClick = {
                            Log.d(TAG, "third IconButton")
                        }) {
                            Icon(Icons.Filled.Settings, contentDescription = null)
                        }
                    }
                )
            },
            drawerContent = {
                drawerContent()
            }
        ) {
            // screen conetnt
            HelloText()
        }
    }

    @Composable
    fun drawerContent() {
        Text("Drawer title@@#@#", modifier = Modifier.padding(16.dp))
        Text("Drawer content 1", modifier = Modifier.padding(16.dp))
        Text("Drawer content 2", modifier = Modifier.padding(16.dp))
        Text("Drawer content 3", modifier = Modifier.padding(16.dp))
    }
}