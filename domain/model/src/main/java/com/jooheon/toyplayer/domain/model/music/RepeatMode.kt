package com.jooheon.toyplayer.domain.model.music

enum class RepeatMode {
    REPEAT_OFF, /** ExoPlayer.REPEAT_MODE_OFF **/
    REPEAT_ONE, /** ExoPlayer.REPEAT_MODE_ONE **/
    REPEAT_ALL, /** ExoPlayer.REPEAT_MODE_ALL **/
    ;
    companion object {
        fun getByValue(value: Int) = entries[value]
    }
}