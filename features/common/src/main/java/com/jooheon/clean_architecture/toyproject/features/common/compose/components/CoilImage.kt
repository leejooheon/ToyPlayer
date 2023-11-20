package com.jooheon.clean_architecture.toyproject.features.common.compose.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.jooheon.clean_architecture.toyproject.features.common.R

@Composable
fun CoilImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(0.dp),
    contentScale: ContentScale = ContentScale.Crop,
    @DrawableRes placeholderRes: Int = R.drawable.ic_placeholder,
    @DrawableRes errorRes: Int = placeholderRes,
) {
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = url).apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
                placeholder(drawableResId = placeholderRes)
                transformations(RoundedCornersTransformation())
                error(drawableResId = errorRes)
            }).build()
        ),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier.clip(shape)
    )
}
