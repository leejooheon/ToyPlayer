package com.jooheon.toyplayer.features.common

import com.bumptech.glide.load.model.GlideUrl

internal class GlideUrlWithCacheKey(
    private val imageUrl: String,
) : GlideUrl(imageUrl) {
    override fun getCacheKey(): String = imageUrl
}