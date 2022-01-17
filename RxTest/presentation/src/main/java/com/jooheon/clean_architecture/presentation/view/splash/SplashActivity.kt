package com.jooheon.clean_architecture.presentation.view.splash

import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.base.BaseActivity
import com.jooheon.clean_architecture.presentation.common.openActivityAndClearStack
import com.jooheon.clean_architecture.presentation.databinding.ActivitySplashBinding
import com.jooheon.clean_architecture.presentation.view.main.MainActivity

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity: BaseActivity<ActivitySplashBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_splash

    val viewModel: SplashViewModel by viewModels()

    override fun initAfterBinding() {
        decideNavigation()
    }

    private fun decideNavigation() {
        Handler(Looper.getMainLooper()).postDelayed({
            val targetActivity = if(viewModel.isFirstLaunched()) {
                MainActivity::class.java
            } else {
                MainActivity::class.java // FIXME: tutorialActivity
            }

            openActivityAndClearStack(targetActivity)
        }, 2000)
    }
}