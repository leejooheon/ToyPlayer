package com.jooheon.toyplayer.features.player.component.legacy

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.media3.common.Player

class ExoVisualizer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Player.Listener {

    val bandView = VisualizerBandView(context, attrs)

    init {
        addView(bandView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }
}