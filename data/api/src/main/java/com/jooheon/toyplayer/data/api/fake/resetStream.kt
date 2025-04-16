package com.jooheon.toyplayer.data.api.fake

import java.io.InputStream


internal fun InputStream.resetStream(): InputStream {
    reset()
    return this
}