package com.jooheon.clean_architecture.presentation.view.main

import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.lifecycle.lifecycleScope
import com.jooheon.clean_architecture.presentation.R

import com.jooheon.clean_architecture.presentation.base.BaseActivity
import com.jooheon.clean_architecture.presentation.base.extensions.setSafeOnClickListener
import com.jooheon.clean_architecture.presentation.databinding.ActivityMainBinding

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val TAG = MainActivity::class.simpleName
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    val viewModel: MainViewModel by viewModels()

    override fun initStartView() {

    }

    @ExperimentalFoundationApi
    override fun initDataBinding() {
        viewDataBinding.btCompose.setSafeOnClickListener {
            lifecycleScope.launchWhenResumed {
                viewModel.replaceComposeFragment(supportFragmentManager)
            }
        }

        viewDataBinding.btRepository.setSafeOnClickListener {
            lifecycleScope.launchWhenResumed {
                viewModel.replaceRepositoryFragment(supportFragmentManager)
            }
        }
    }

    override fun initAfterBinding() {
        viewModel.replaceHomeFragment(supportFragmentManager)
    }

    override fun getNavControllerId(): Int = 0
}