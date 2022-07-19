package com.jooheon.clean_architecture.presentation.service.music

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jooheon.clean_architecture.presentation.MainActivity
import java.lang.ref.WeakReference

class MusicStateReceiver(activity: MainActivity) : BroadcastReceiver() {
    private val TAG = MusicStateReceiver::class.java.simpleName
    private val reference: WeakReference<MainActivity> = WeakReference(activity)

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val activity = reference.get()
        if (activity != null && action != null) {
            Log.d(TAG, "action: $action")
//            when (action) {
//                FAVORITE_STATE_CHANGED -> activity.onFavoriteStateChanged()
//                META_CHANGED -> activity.onPlayingMetaChanged()
//                QUEUE_CHANGED -> activity.onQueueChanged()
//                PLAY_STATE_CHANGED -> activity.onPlayStateChanged()
//                REPEAT_MODE_CHANGED -> activity.onRepeatModeChanged()
//                SHUFFLE_MODE_CHANGED -> activity.onShuffleModeChanged()
//                MEDIA_STORE_CHANGED -> activity.onMediaStoreChanged()
//            }
        }
    }
}