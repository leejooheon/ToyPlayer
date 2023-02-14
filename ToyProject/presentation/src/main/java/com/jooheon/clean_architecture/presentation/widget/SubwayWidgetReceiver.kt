package com.jooheon.clean_architecture.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class SubwayWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = SubwayWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        SubwayWidgetUpdateWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        SubwayWidgetUpdateWorker.cancel(context)
    }
}