package com.jooheon.clean_architecture.features.musicservice.data

class RingBuffer<T>(val size: Int, init: (index: Int) -> T) {
    private val list = MutableList(size, init)

    private var index = 0

    fun getOrNull(index: Int): T? = list.getOrNull(index)

    fun append(element: T) = list.set(index++ % size, element)
    fun clear(default: T) {
        repeat(size) { append(default)}
    }
}
