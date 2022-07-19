package com.jooheon.clean_architecture.presentation.service.music

import android.support.v4.media.session.MediaSessionCompat

/**
 * Created by hemanths on 2019-08-01.
 */

class MediaSessionCallback(
    private val musicService: MusicService,
) : MediaSessionCompat.Callback(){

}