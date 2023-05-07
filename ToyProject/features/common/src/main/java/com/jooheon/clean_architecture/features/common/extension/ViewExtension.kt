package com.jooheon.clean_architecture.features.common.extension

import android.view.View

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
}