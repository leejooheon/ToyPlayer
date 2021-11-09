package com.example.rxtest.main

import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.rxtest.R
import com.example.rxtest.base.BaseActivity
import com.example.rxtest.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    override val viewModel: MainViewModel = MainViewModel()

    override fun initStartView() {

    }

    override fun initDataBinding() {
        viewModel.repositoryLiveData.observe(this, Observer {
            viewDataBinding.tvRepository.setText(it);
        })
    }

    override fun initAfterBinding() {
        viewDataBinding.btRepository.setOnClickListener {
            val owner = viewDataBinding.etRepository.text.toString()
            viewModel.onClickRepository(owner)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}