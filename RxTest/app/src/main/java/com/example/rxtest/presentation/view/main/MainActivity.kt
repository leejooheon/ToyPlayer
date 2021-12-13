package com.example.rxtest.presentation.view.main

import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.rxtest.R
import com.example.rxtest.presentation.base.BaseActivity
import com.example.rxtest.databinding.ActivityMainBinding
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private val TAG = MainActivity::class.simpleName
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    override val viewModel: MainViewModel = MainViewModel()

    override fun initStartView() {

    }

    @ExperimentalFoundationApi
    override fun initDataBinding() {
        // 버튼 클릭 리스너 observable
        viewDataBinding.btRepository.clicks()
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()) // avoid multi click
            .subscribe {
                viewModel.replaceRepositoryFragment(supportFragmentManager)
            }

        viewDataBinding.btCompose.clicks()
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe {
                viewModel.replaceComposeFragment(supportFragmentManager)
            }
    }

    @ExperimentalFoundationApi
    override fun initAfterBinding() {
        viewModel.replaceComposeFragment(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}