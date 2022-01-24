package com.jooheon.clean_architecture.presentation.view.splash

import android.app.Activity
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
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
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.base.BaseComposeActivity
import com.jooheon.clean_architecture.presentation.common.openActivityAndClearStack
import com.jooheon.clean_architecture.presentation.theme.SplashTheme
import com.jooheon.clean_architecture.presentation.view.main.MainActivity

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity: BaseComposeActivity() {
    val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen()
        }
    }

    @Composable
    fun SplashScreen() {
        SplashTheme() {
            val scale = remember { Animatable(0f) }

            LaunchedEffect(true) {
                scale.animateTo(
                    targetValue = 0.7f,
                    animationSpec = tween(
                        durationMillis = 800,
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

            PrepareRecomposableHandler()
        }
    }


    @Composable
    fun PrepareRecomposableHandler() {
        when(viewModel.done.value) {
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
                openActivityAndClearStack(MainActivity::class.java)
            }
        }
    }

    @Preview
    @Composable
    fun PreviewSplashScreen() {
        SplashTheme() {
            val scale = remember { Animatable(0f) }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
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
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}