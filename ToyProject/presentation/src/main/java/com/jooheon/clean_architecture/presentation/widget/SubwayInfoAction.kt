package com.jooheon.clean_architecture.presentation.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class SubwayInfoAction: ActionCallback {
    private val TAG = SubwayInfoAction::class.java.simpleName

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d(TAG, "onClick!!!")
        SubwayWidgetUpdateWorker.enqueue(context)
    }
}