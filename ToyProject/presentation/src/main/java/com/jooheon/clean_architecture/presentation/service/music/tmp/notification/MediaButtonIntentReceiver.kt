package com.jooheon.clean_architecture.presentation.service.music.tmp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import com.jooheon.clean_architecture.presentation.service.music.MusicService


class MediaButtonIntentReceiver: BroadcastReceiver() {
    private val TAG = MusicService::class.java.simpleName + "@" + MediaButtonIntentReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        val action = SongAction.values()[intent.action?.toInt() ?: SongAction.NOTHING.ordinal]
        Log.d(TAG, "$TAG - onRecived")

        if(isValidIntent(intent)) {
            startService(context, action.toString())
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

    private fun startService(context: Context, action: String) {
        val intent = Intent(context, MusicService::class.java)
        intent.action = action
        try {
            // IMPORTANT NOTE: (kind of a hack)
            // on Android O and above the following crashes when the app is not running
            // there is no good way to check whether the app is running so we catch the exception
            // we do not always want to use startForegroundService() because then one gets an ANR
            // if no notification is displayed via startForeground()
            // according to Play analytics this happens a lot, I suppose for example if command = PAUSE
            context.startService(intent)
        } catch (ignored: IllegalStateException) {
            ContextCompat.startForegroundService(context, intent)
        }
    }

    companion object {
        const val TOYPROJECT_PACKAGE_NAME = "com.jooheon.toyproject"
        const val ACTION_QUIT = "$TOYPROJECT_PACKAGE_NAME.quitservice"
        const val ACTION_STOP = "$TOYPROJECT_PACKAGE_NAME.stop"
        const val ACTION_TOGGLE_PAUSE = "$TOYPROJECT_PACKAGE_NAME.togglepause"
        const val ACTION_REWIND = "$TOYPROJECT_PACKAGE_NAME.rewind"
        const val ACTION_SKIP = "$TOYPROJECT_PACKAGE_NAME.skip"
        const val ACTION_PAUSE = "$TOYPROJECT_PACKAGE_NAME.pause"
        const val ACTION_PLAY = "$TOYPROJECT_PACKAGE_NAME.play"
    }
}