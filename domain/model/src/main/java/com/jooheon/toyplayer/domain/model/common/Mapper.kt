package com.jooheon.toyplayer.domain.model.common

abstract class Mapper<A ,B> {
    abstract fun map(data: A): B
    abstract fun mapInverse(data: B): A
}