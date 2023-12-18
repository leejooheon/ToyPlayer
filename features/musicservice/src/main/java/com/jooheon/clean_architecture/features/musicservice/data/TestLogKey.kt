package com.jooheon.clean_architecture.features.musicservice.data

import kotlin.random.Random

class TestLogKey {
    companion object {
        private const val MAX_LENGTH = 32

        fun random(): String {
            val generator = Random(System.currentTimeMillis())
            val randomStringBuilder = StringBuilder()
            val randomLength: Int = generator.nextInt(MAX_LENGTH)
            var tempChar: Char
            for (i in 0 until randomLength) {
                tempChar = ((generator.nextInt(96) + 32).toChar())
                randomStringBuilder.append(tempChar)
            }
            return randomStringBuilder.toString()
        }
    }
}