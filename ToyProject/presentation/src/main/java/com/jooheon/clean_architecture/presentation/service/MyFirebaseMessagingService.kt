package com.jooheon.clean_architecture.presentation.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jooheon.clean_architecture.domain.usecase.firebase.FirebaseTokenUseCase
import com.jooheon.clean_architecture.presentation.MainActivity
import com.jooheon.clean_architecture.presentation.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@AndroidEntryPoint
class MyFirebaseMessagingService: FirebaseMessagingService() {
    @Inject lateinit var firebaseTokenUseCase: FirebaseTokenUseCase

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        private const val NOTIFICATION_ID = 9999
        private const val SCHEME_LINK = "in_link"
        private const val CHANNEL = "kt_caremon"
    }


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        firebaseTokenUseCase.setToken(newToken)
        Log.d(TAG, "set token: $newToken")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived remoteMessage : " + remoteMessage);
        Log.d(TAG, "From : " + remoteMessage.getFrom());
        Log.d(TAG, "Data : " + remoteMessage.getData());
        Log.d(TAG, "Notification : " + remoteMessage.getNotification());

        sendNotification(remoteMessage)
    }
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification!!.title
        val message = remoteMessage.notification!!.body
        val data = remoteMessage.data
        var schemeLink = ""

        if (data.containsKey("in_link") && !data["in_link"].isNullOrEmpty()) {
            schemeLink = data["in_link"]!!
        }
        Log.d(TAG, "Title : $title")
        Log.d(TAG, "Message : $message")
        Log.d(TAG, "Scheme Link : $schemeLink")

        val pendingIntent: PendingIntent = getPendingIntent(schemeLink)

        val channel_nm = "Notificatoin"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notiChannel = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channelMessage = NotificationChannel(
                CHANNEL,
                channel_nm,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channelMessage.description = "description"
            channelMessage.enableLights(true)
            channelMessage.enableVibration(true)
            channelMessage.setShowBadge(false)
            channelMessage.vibrationPattern = longArrayOf(1000, 1000)
            notiChannel.createNotificationChannel(channelMessage)

            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title) //푸시알림의 제목
                .setContentText(message) //푸시알림의 내용
                .setChannelId(CHANNEL)
                .setAutoCancel(true) //선택시 자동으로 삭제되도록 설정.
                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                notificationBuilder.addAction(getNotificationIcon(), "launch", pendingIntent)
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        } else {
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, "")
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent) //앱으로 보낼 data
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun getNotificationIcon(): Int {
        return R.drawable.ic_launcher_foreground
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getPendingIntent(schemeLink: String): PendingIntent {
        Log.d(TAG, "schemeLink: $schemeLink")
        val trampolineIntent = Intent(this, MainActivity::class.java)
        trampolineIntent.putExtra(SCHEME_LINK, schemeLink)
        return PendingIntent.getActivity(
            applicationContext, // TODO: injection으로 교체해야해야하는지 확인
            0,
            trampolineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}