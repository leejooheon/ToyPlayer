package com.jooheon.toyplayer.features.musicservice.player

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
class ToyPlayer(player: Player): ForwardingPlayer(player){
    override fun play() {
        if(isCurrentMediaItemLive) seekToDefaultPosition()
        super.play()
    }

    override fun seekToPrevious() {
        if(isCurrentMediaItemLive) seekToPreviousMediaItem()
        else super.seekToPrevious()
    }

    override fun seekToNext() {
        if(hasNextMediaItem()) {
            super.seekToNext()
        } else {
            seekTo(0, C.TIME_UNSET)
        }
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