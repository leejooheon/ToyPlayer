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
import androidx.navigation.NavController
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation

@Composable
fun SplashScreen(
    navcontroller: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
    scaleInitialValue: Float = 0f
) {
    val scale = remember { Animatable(scaleInitialValue) }

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

    PrepareRecomposableHandler(navcontroller, viewModel)
}


@Composable
internal fun PrepareRecomposableHandler(
    navigator: NavController,
    viewModel: SplashViewModel
) {
    val step = viewModel.done.value

    when(step) {
        is SplashResult.Default -> {
            viewModel.prepareLaunchApp(LocalContext.current)
        }

        is SplashResult.NetworkAvailable -> {
            // it will be later
        }

        is SplashResult.ServiceAvailable -> {
            // it will be later
        }

        is SplashResult.Update -> {
            // it will be later
        }

        is SplashResult.Account -> {
            // it will be later
        }

        is SplashResult.Permisison -> {
            // it will be later
        }

        is SplashResult.Tutorial -> {
            // it will be later
        }

        is SplashResult.Done -> {
            navigator.navigate(ScreenNavigation.Main) {
                launchSingleTop = true
                popUpTo(ScreenNavigation.Splash) {
                    inclusive = true
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {
    val viewModel = SplashViewModel()
    val context = LocalContext.current
    SplashScreen(NavController(context), viewModel, 0.85f)
}