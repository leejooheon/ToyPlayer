package com.jooheon.toyplayer.domain.model.music


enum class EqualizerType(val numBands: Int) {
    BAND_03(3),
    BAND_05(5),
    BAND_10(10),
    BAND_15(15),
    BAND_31(31),
    ;

    fun frequencies(): List<Float> = when(this) {
        BAND_03 -> Band03Frequencies
        BAND_05 -> Band05Frequencies
        BAND_10 -> Band10Frequencies
        BAND_15 -> Band15Frequencies
        BAND_31 -> Band31Frequencies
    }

    override fun toString(): String = when(this) {
        BAND_31 -> "$numBands (experimental)"
        else -> numBands.toString()
    }

    companion object {
        val default = BAND_10

        private val Band03Frequencies = listOf(63f, 630f, 8000f)
        private val Band05Frequencies = listOf(40f, 160f, 630f, 2500f, 10000f)
        private val Band10Frequencies = listOf(31.5f, 63f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)
        private val Band15Frequencies = listOf(25f, 40f, 63f, 100f, 160f, 250f, 400f, 630f, 1000f, 1600f, 2500f, 4000f, 6300f, 10000f, 16000f)
        private val Band31Frequencies = listOf(20f, 25f, 31.5f, 40f, 50f, 63f, 80f, 100f, 125f, 160f, 200f, 250f, 315f, 400f, 500f, 630f, 800f, 1000f, 1300f, 1600f, 2000f, 2500f, 3200f, 4000f, 5000f, 6300f, 8000f, 10000f, 12500f, 16000f, 20000f)

        fun Int.toType(): EqualizerType = when(this) {
            BAND_03.numBands -> BAND_03
            BAND_05.numBands -> BAND_05
            BAND_10.numBands -> BAND_10
            BAND_15.numBands -> BAND_15
            BAND_31.numBands -> BAND_31
            else -> EqualizerType.default
        }
    }
}
