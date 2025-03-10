package com.jooheon.toyplayer.features.player.component.inside

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jooheon.toyplayer.core.designsystem.component.CustomGlideImage
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Drawables

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun InsidePagerItem(
    image: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    if(LocalInspectionMode.current) {
        Image(
            painter = painterResource(Drawables.placeholder_600x400),
            contentDescription = null,
            modifier = modifier,
        )
    }
    CustomGlideImage(
        model = image,
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PreviewInsidePagerItem() {
    ToyPlayerTheme {
        InsidePagerItem(
            image = "",
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )
    }
}
