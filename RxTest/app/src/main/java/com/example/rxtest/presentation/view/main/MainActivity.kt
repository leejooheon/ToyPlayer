package com.example.rxtest.presentation.view.main

import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.lifecycle.lifecycleScope

import com.example.rxtest.R
import com.example.rxtest.presentation.base.BaseActivity
import com.example.rxtest.databinding.ActivityMainBinding
import com.example.rxtest.presentation.base.extensions.setSafeOnClickListener

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

    @ExperimentalFoundationApi
    override fun initAfterBinding() {

    }

    override fun getNavControllerId(): Int = 0
}