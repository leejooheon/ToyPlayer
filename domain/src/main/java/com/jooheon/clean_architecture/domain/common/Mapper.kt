package com.jooheon.clean_architecture.domain.common
abstract class Mapper<A ,B> {
    abstract fun map(data: A): B
    abstract fun mapInverse(data: B): A
}