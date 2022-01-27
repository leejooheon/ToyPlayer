package com.jooheon.clean_architecture.presentation.view.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner

import com.jooheon.clean_architecture.presentation.base.BaseComposeActivity
import com.jooheon.clean_architecture.presentation.theme.ApplicationTheme
import com.jooheon.clean_architecture.presentation.view.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {
    private val TAG = MainActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppContent()
        }

        setOwners()
    }

    @Composable
    private fun AppContent() {
        ApplicationTheme() {
            DestinationsNavHost(navGraph = NavGraphs.root)
        }
    }
}

private fun ComponentActivity.setOwners() { // TODO: 이게 뭐지??
    val decorView = window.decorView
    if (ViewTreeLifecycleOwner.get(decorView) == null) {
        ViewTreeLifecycleOwner.set(decorView, this)
    }
    if (ViewTreeViewModelStoreOwner.get(decorView) == null) {
        ViewTreeViewModelStoreOwner.set(decorView, this)
    }
    if (ViewTreeSavedStateRegistryOwner.get(decorView) == null) {
        ViewTreeSavedStateRegistryOwner.set(decorView, this)
    }
}
/*
suspend fun fcmRegister() {
    val fcmToken = try {
        suspendCoroutine<String> {
            firebaseMessage.token
                .addOnSuccessListener { fcmToken ->
                    it.resume(fcmToken)
                }
                .addOnFailureListener { t ->
                    it.resumeWithException(t)
                }
        }
    } catch (t: Throwable) {
        firebaseCrashlytics.recordException(t)
        return
    }

    restApiService.register(fcmToken)
}
 */