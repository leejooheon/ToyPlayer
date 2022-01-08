package com.jooheon.clean_architecture.presentation.view.temp.compose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.jooheon.clean_architecture.presentation.view.main.MainActivity

@ExperimentalFoundationApi
class ComposeFragment: Fragment(){
    private val TAG = ComposeFragment::class.simpleName
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BottomBar()
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
    fun BottomBar() {
        Scaffold(
            isFloatingActionButtonDocked = false,
            topBar = {
                TopAppBar(
                    title = { Text(text = "this is topAppBar") },
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
                            val intent = Intent(context, ComposeActivity::class.java)
                            startActivity(intent)
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
                DrawerContent()
            }
        ) {
            // screen conetnt
            HelloText()
        }
    }

    @Composable
    fun DrawerContent() {
        Text("Drawer title@@#@#", modifier = Modifier.padding(16.dp))
        Text("Drawer content 1", modifier = Modifier.padding(16.dp))
        Text("Drawer content 2", modifier = Modifier.padding(16.dp))
        Text("Drawer content 3", modifier = Modifier.padding(16.dp))
    }
}