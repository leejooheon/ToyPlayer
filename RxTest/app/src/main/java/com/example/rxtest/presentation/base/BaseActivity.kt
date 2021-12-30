package com.example.rxtest.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity<T : ViewDataBinding, R : BaseViewModel> : DaggerBaseActivity() {

    lateinit var viewDataBinding: T
    abstract val viewModel: R
    abstract val layoutResourceId: Int

    abstract fun initStartView() // 레이아웃을 띄운 직후 호출. 뷰나 액티비티의 속성 등을 초기화. ex) 리사이클러뷰, 툴바, 드로어뷰..
    abstract fun initDataBinding() // 두번째로 호출. 데이터 바인딩 및 rxjava 설정. ex) rxjava observe, databinding observe..
    abstract fun initAfterBinding() // 바인딩 이후에 할 일을 여기에 구현. 그 외에 설정할 것이 있으면 이곳에서 설정. 클릭 리스너도 이곳에서 설정.

    private val navController: NavController by lazy {
        findNavController(getNavControllerId())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding = DataBindingUtil.setContentView(this, layoutResourceId)

        initStartView()
        initDataBinding()
        initAfterBinding()
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
    abstract fun getNavControllerId(): Int
}