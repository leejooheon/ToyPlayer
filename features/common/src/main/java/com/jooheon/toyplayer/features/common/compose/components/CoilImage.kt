package com.jooheon.toyplayer.features.common.compose.components

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.graphics.drawable.toDrawable
import androidx.palette.graphics.Palette
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.jooheon.toyplayer.core.resources.Drawables
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.ResourceError
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import timber.log.Timber

@Composable
fun CoilImage2(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(0.dp),
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Drawable = MaterialTheme.colorScheme.surface.toArgb().toDrawable(),
    @DrawableRes errorRes: Int = Drawables.ic_broken_image,
    onResourceReady: ((Result<Int, ResourceError>) -> Unit)? = null,
) {
    if(LocalInspectionMode.current) {
        Image(
            painter = painterResource(Drawables.placeholder_600x400),
            contentDescription = null,
            modifier = modifier,
        )
        val color = MaterialTheme.colorScheme.surface.toArgb()
        onResourceReady?.invoke(Result.Success(color))
        return
    }

    val context = LocalContext.current
    val request = ImageRequest.Builder(context)
        .data(data = url)
        .apply(
            block = fun ImageRequest.Builder.() {
                crossfade(true)
                placeholder(drawable = placeholder)
                error(drawableResId = errorRes)
                transformations(RoundedCornersTransformation())
            }
        ).build()
    val imageLoader = context.imageLoader.newBuilder()
        .crossfade(1000)
        .build()

    SubcomposeAsyncImage(
        modifier = modifier.clip(shape),
        contentDescription = contentDescription,
        contentScale = contentScale,
        imageLoader = imageLoader,
        model = request,
        error = {
            Image(
                painter = rememberAsyncImagePainter(
                    model = request,
                    imageLoader = imageLoader
                ),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier.clip(shape)
            )
            val error = ResourceError.Glide(it.result.throwable.message.defaultEmpty())
            onResourceReady?.invoke(Result.Error(error))
        },
        success = {
            val bitmap = it.result.drawable.toBitmapOrNull() ?: run {
                val error = ResourceError.Glide("bitmap is null")
                onResourceReady?.invoke(Result.Error(error))
                return@SubcomposeAsyncImage
            }
            getMainColorByBitmap(
                bitmap = bitmap,
                colorDefault = MaterialTheme.colorScheme.primary.toArgb(),
                callback = { dominateColor ->
                    dominateColor?.let {
                        Timber.d("dominateColor: $it")
                        onResourceReady?.invoke(Result.Success(it))
                    } ?: run {
                        val error = ResourceError.Glide("dominateColor is null")
                        onResourceReady?.invoke(Result.Error(error))
                    }
                }
            )
        }
    )
}

private fun getMainColorByBitmap(
    bitmap: Bitmap,
    colorDefault: Int,
    callback: (Int?) -> Unit,
) {
    Palette.from(bitmap).generate { palette ->
        val dominantColor = palette?.getDominantColor(colorDefault)
        callback(dominantColor)
    }
}