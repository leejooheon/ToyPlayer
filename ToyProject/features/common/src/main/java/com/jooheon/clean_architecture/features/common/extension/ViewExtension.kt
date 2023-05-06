package com.jooheon.clean_architecture.features.common.extension

import android.view.View
import com.airbnb.lottie.LottieAnimationView

object ViewExtension {
    fun View.gone() { this.visibility = View.GONE }
    fun View.visible() { this.visibility = View.VISIBLE }
    fun View.invisible() { this.visibility = View.INVISIBLE }
    fun View.setVisibility(flag: Boolean, isGone: Boolean = true) {
        if(flag) visible()
         else {
            if (isGone) gone() else invisible()
        }
    }


    fun LottieAnimationView.playWith(
        progress: Pair<Float, Float>,
        speed: Float = 2f
    ) {
//        val currentProgress = ((getProgress() * 100.0).roundToInt() / 100.0).toFloat()
        this.speed = speed
        setMinAndMaxProgress(progress.first, progress.second)
        playAnimation()
    }
}