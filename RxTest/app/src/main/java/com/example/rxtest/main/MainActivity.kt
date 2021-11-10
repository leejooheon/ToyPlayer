package com.example.rxtest.main

import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.rxtest.R
import com.example.rxtest.base.BaseActivity
import com.example.rxtest.databinding.ActivityMainBinding
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private val TAG = MainActivity::class.simpleName
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    override val viewModel: MainViewModel = MainViewModel()

    override fun initStartView() {

    }

    override fun initDataBinding() {
        viewModel.repositoryLiveData.observe(this, Observer {
            viewDataBinding.tvRepository.setText(it);
        })

        // 버튼 클릭 리스너 observable
        viewDataBinding.btRepository.clicks()
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()) // avoid multi click
            .subscribe {
                val owner = viewDataBinding.etRepository.text.toString()
                viewModel.callRepositoryApi(owner)
            }

        // EditText Observable, 1초에 한번 Trigger 된다.
        val etRepositoryObservable = viewDataBinding.etRepository.textChanges()
        etRepositoryObservable.debounce(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe({
                viewModel.callRepositoryApi(it.toString())
            })
    }

    override fun initAfterBinding() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}