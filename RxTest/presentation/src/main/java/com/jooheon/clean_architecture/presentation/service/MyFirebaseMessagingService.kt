package com.jooheon.clean_architecture.presentation.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jooheon.clean_architecture.domain.usecase.firebase.FirebaseTokenUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService: FirebaseMessagingService() {
    @Inject lateinit var firebaseTokenUseCase: FirebaseTokenUseCase

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        firebaseTokenUseCase.setToken(newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived remoteMessage : " + remoteMessage);
        Log.d(TAG, "From : " + remoteMessage.getFrom());
        Log.d(TAG, "Data : " + remoteMessage.getData());
        Log.d(TAG, "Notification : " + remoteMessage.getNotification());
    }
}