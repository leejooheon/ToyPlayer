package com.jooheon.toyplayer.features.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Size
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

object GlideUtil {
    enum class ImageCacheStrategy {
        ALL, DATA, NONE, RESOURCE, AUTOMATIC;
        fun toGlide() = when(this) {
            ImageCacheStrategy.ALL -> DiskCacheStrategy.ALL
            ImageCacheStrategy.DATA -> DiskCacheStrategy.DATA
            ImageCacheStrategy.NONE -> DiskCacheStrategy.NONE
            ImageCacheStrategy.RESOURCE -> DiskCacheStrategy.RESOURCE
            ImageCacheStrategy.AUTOMATIC -> DiskCacheStrategy.AUTOMATIC
        }
    }
    enum class Crop {
        Center, Circle, None;
    }

    fun loadBitmap(
        context: Context,
        uri: Uri,
        size: Size,
        strategy: ImageCacheStrategy = ImageCacheStrategy.AUTOMATIC,
        crop: Crop = Crop.Center,
        onDone: (Bitmap?) -> Unit,
    ) {
        var builder = Glide.with(context)
            .asBitmap()
            .diskCacheStrategy(strategy.toGlide())
            .load(uri)

        builder = when(crop) {
            Crop.Center -> builder.centerCrop()
            Crop.Circle -> builder.circleCrop()
            Crop.None -> builder
        }

        builder.into(object : CustomTarget<Bitmap>(size.width, size.height) {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                onDone.invoke(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                onDone.invoke(null)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                onDone.invoke(null)
            }
        })
    }
}