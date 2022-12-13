package com.jooheon.clean_architecture.presentation.service.music.tmp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import com.jooheon.clean_architecture.presentation.service.music.MusicService


class MediaButtonIntentReceiver: MediaButtonReceiver() {
    private val TAG = MusicService::class.java.simpleName + "@" + MediaButtonIntentReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        val action = SongAction.values()[intent.action?.toInt() ?: SongAction.NOTHING.ordinal]
        Log.d(TAG, "$TAG - onRecived")

        if(isValidIntent(intent)) {
            val serviceIntent = Intent(context, MusicService::class.java)
            serviceIntent.action = action.toString()
            MusicService.startService(context, serviceIntent)
        }
    }


    private fun isValidIntent(intent: Intent): Boolean {
        val intentAction = intent.action

        val event = if(Intent.ACTION_MEDIA_BUTTON == intentAction)
            intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT) as? KeyEvent
        else null

        event?.let {
            val songAction = SongAction.values()[intent.action?.toInt() ?: SongAction.NOTHING.ordinal]
            val action = it.action
            val repeatCount = it.repeatCount

            if(songAction != SongAction.NOTHING &&
                action == KeyEvent.ACTION_DOWN &&
                repeatCount == 0
            ) {
                return true
            }
        }
        return false
    }
}