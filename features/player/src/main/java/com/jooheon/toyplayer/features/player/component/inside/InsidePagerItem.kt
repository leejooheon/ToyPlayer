package com.jooheon.toyplayer.features.player.component.inside

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.common.compose.components.CustomGlideImage

@Composable
internal fun InsidePagerItem(
    image: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    CustomGlideImage(
        url = image,
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
