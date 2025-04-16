package com.jooheon.toyplayer.features.common.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Size
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

fun loadGlideBitmap(
    context: Context,
    uri: Uri,
    size: Size,
    onDone: (Bitmap?) -> Unit,
) {
    val builder = Glide.with(context)
        .asBitmap()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .centerCrop()
        .load(uri)

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