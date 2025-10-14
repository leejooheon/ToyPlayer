package com.jooheon.toyplayer.features.musicservice.player

import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

@UnstableApi
class TestPlayer(
    player: Player
): ForwardingPlayer(player) {

    override fun play() {
        super.play()
    }
}