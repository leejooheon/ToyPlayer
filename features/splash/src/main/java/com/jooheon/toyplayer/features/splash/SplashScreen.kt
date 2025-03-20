package com.jooheon.toyplayer.features.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.commonui.ext.observeWithLifecycle
import com.jooheon.toyplayer.features.splash.model.SplashEvent
import com.jooheon.toyplayer.features.splash.model.SplashState


@Composable
fun SplashScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    viewModel.state.observeWithLifecycle {
        when(it) {
            is SplashState.Default -> viewModel.dispatch(SplashEvent.CheckNetwork)
            is SplashState.NetworkAvailable -> {
                if(it.value) viewModel.dispatch(SplashEvent.ServiceCheck)
                else { /** do something **/ }
            }
            is SplashState.ServiceAvailable -> {
                if(it.value) viewModel.dispatch(SplashEvent.Update(context))
                else { /** do something **/ }
            }
            is SplashState.Update -> { /** not reached **/ }
            is SplashState.Done -> navigateTo.invoke(ScreenNavigation.Player)
        }
    }

    SplashScreenInternal()
}

@Composable
private fun SplashScreenInternal() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 3.dp
        )
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {
    SplashScreenInternal()
}