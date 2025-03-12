package com.jooheon.toyplayer.features.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
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
    val scale = remember { Animatable(0f) }

    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 0.85f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                }
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_toys_24),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .scale(scale.value)
                .size(150.dp)
        )

        Text(
            modifier = Modifier
                .padding(top = 6.dp)
                .scale(scale.value),
            text = "My Toy Project",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {
    SplashScreenInternal()
}