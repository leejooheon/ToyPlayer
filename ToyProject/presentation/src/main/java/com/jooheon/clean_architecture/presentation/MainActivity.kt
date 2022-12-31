package com.jooheon.clean_architecture.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.jooheon.clean_architecture.presentation.base.BaseComposeActivity
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicPlayerViewModel
import com.jooheon.clean_architecture.presentation.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.presentation.view.navigation.FullScreenNavigationHost

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {
    private val musicPlayerViewModel: MusicPlayerViewModel by viewModels()
    private var serviceToken: MusicPlayerViewModel.ServiceToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceToken = musicPlayerViewModel.bindToService(this)
        Log.d(TAG, "viewModel: ${musicPlayerViewModel}")
        setContent {
            AppContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayerViewModel.unbindToService(serviceToken)
    }

    @Composable
    private fun AppContent() {
        ApplicationTheme() {
//            DestinationsNavHost(navGraph = NavGraphs.root)
            FullScreenNavigationHost(
                modifier = Modifier.fillMaxSize()
            )
        }

    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}