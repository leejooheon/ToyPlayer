package com.jooheon.toyplayer.core.designsystem.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.core.graphics.drawable.toBitmapOrNull
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jooheon.toyplayer.core.resources.Colors
import androidx.core.graphics.drawable.toDrawable
import androidx.palette.graphics.Palette
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.ResourceError
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import timber.log.Timber

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CustomGlideImage(
    modifier: Modifier,
    model: String,
    contentDescription: String,
    colorDefault: Color = colorResource(Colors.color_default),
    contentScale: ContentScale = ContentScale.Crop,
    transition: CrossFade? = CrossFade(tween(1000)),
    loading: Placeholder? = null,
    failure: Placeholder? = null,
    onResourceReady: ((Result<Int, ResourceError>) -> Unit)? = null,
) {
    val placeholder = placeholder(colorDefault.toArgb().toDrawable())
    var backgroundColor by remember { mutableIntStateOf(colorDefault.toArgb())}

    GlideImage(
        model = model,
        contentDescription = contentDescription,
        contentScale = contentScale,
        transition = transition,
        loading = loading ?: placeholder,
        failure = failure ?: placeholder,
        modifier = modifier.background(Color(backgroundColor)),
        requestBuilderTransform = { requestBuilder ->
            requestBuilder.listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    val error = ResourceError.Glide(e?.message.defaultEmpty())
                    onResourceReady?.invoke(Result.Error(error))
                    return true
                }
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    val bitmap = resource.toBitmapOrNull() ?: run {
                        val error = ResourceError.Glide("bitmap is null")
                        onResourceReady?.invoke(Result.Error(error))
                        return true
                    }
                    getMainColorByBitmap(
                        bitmap = bitmap,
                        colorDefault = colorDefault.toArgb(),
                        callback = { dominateColor ->
                            dominateColor?.let {
                                backgroundColor = it
                                onResourceReady?.invoke(Result.Success(it))
                            } ?: run {
                                val error = ResourceError.Glide("dominateColor is null")
                                onResourceReady?.invoke(Result.Error(error))
                            }
                        }
                    )
                    return true
                }
            })
        },
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