package com.jooheon.clean_architecture.presentation.service.music

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat

class MusicService: MediaBrowserServiceCompat() {
    private lateinit var storage: PersistentStorage

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot {
        val browserRootPath = MEDIA_ID_ROOT
        return BrowserRoot(browserRootPath, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>,
    ) {
        TODO("Not yet implemented")

        result.sendResult(listOf(storage.recentSong()))
    }
    companion object {
        const val MEDIA_ID_ROOT = "__ROOT__"
    }
}