package com.jooheon.toyplayer.core.juce

class JuceInitializer {
    companion object {
        init {
            System.loadLibrary("JuceEQ")
        }
    }

    external fun initialize()
}