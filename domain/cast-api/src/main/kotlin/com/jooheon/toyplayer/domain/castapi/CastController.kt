package com.jooheon.toyplayer.domain.castapi

interface CastController {
    /** 재생/정지/일시정지 등 제어 */
    fun play(uri: String, seekTo: Long = -1)
    fun pause()
    fun stop()
    fun resume()
    fun seekTo(positionMs: Long)

    fun shuffleModeEnabled(enabled: Boolean)
    fun repeatMode(mode: Int)
}