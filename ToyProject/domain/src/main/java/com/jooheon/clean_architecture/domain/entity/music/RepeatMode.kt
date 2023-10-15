package com.jooheon.clean_architecture.domain.entity.music

enum class RepeatMode {
    REPEAT_OFF, /** ExoPlayer.REPEAT_MODE_OFF **/
    REPEAT_ONE, /** ExoPlayer.REPEAT_MODE_ONE **/
    REPEAT_ALL, /** ExoPlayer.REPEAT_MODE_ALL **/
    ;
//    RepeatMode.values()[ordinal]
    companion object {
        fun getByValue(value: Int) = RepeatMode.values()[value]
    }
}