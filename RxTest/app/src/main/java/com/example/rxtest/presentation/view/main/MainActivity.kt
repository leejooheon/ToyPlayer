package com.example.rxtest.presentation.view.main

import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.example.rxtest.R
import com.example.rxtest.presentation.base.BaseActivity
import com.example.rxtest.databinding.ActivityMainBinding
import com.example.rxtest.di.MyApplication
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private val TAG = MainActivity::class.simpleName
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

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
//        viewModel.replaceComposeFragment(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getNavControllerId(): Int = 0
}