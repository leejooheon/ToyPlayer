package com.jooheon.clean_architecture.presentation.view.custom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jooheon.clean_architecture.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme

@Composable
fun RepositoryImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp
) {
    CustomSurface(
        color = Color.LightGray,
        elevation = elevation,
        shape = CircleShape,
        modifier = modifier
    ) {
        CoilImage(
            url = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview
@Composable
fun RepositoryImagePreview() {
    PreviewTheme(true) {
        Box(
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
        ) {
            RepositoryImage(
                imageUrl = "https://source.unsplash.com/0u_vbeOkMpk",
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}