package com.jooheon.toyplayer.features.musicservice.player

import androidx.annotation.OptIn
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.features.musicservice.ext.forceEnqueue
import com.jooheon.toyplayer.features.musicservice.ext.shuffledItems
import kotlinx.coroutines.CoroutineScope

@OptIn(UnstableApi::class)
class ToyPlayer(player: Player): ForwardingPlayer(player){
    override fun play() {
        if(isCurrentMediaItemLive) seekToDefaultPosition()
        super.play()
    }

    override fun getAvailableCommands(): Player.Commands {
        return super.getAvailableCommands().buildUpon()
            .add(Player.COMMAND_SEEK_TO_NEXT)
            .add(Player.COMMAND_SEEK_TO_PREVIOUS)
            .build()
    }

    override fun isCommandAvailable(command: Int): Boolean {
        // https://github.com/androidx/media/issues/140
        val available = when(command) {
            COMMAND_SEEK_TO_NEXT -> true
            COMMAND_SEEK_TO_PREVIOUS -> true
            else -> super.isCommandAvailable(command)
        }

        return available
    }
}