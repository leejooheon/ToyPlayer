package com.example.rxtest.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding, R : BaseViewModel>: Fragment() {

    lateinit var viewDataBinding: T
    abstract val viewModel: R
    abstract val layoutResourceId: Int

    abstract fun initStartView() // 레이아웃을 띄운 직후 호출. 뷰나 액티비티의 속성 등을 초기화. ex) 리사이클러뷰, 툴바, 드로어뷰..
    abstract fun initDataBinding() // 두번째로 호출. 데이터 바인딩 및 rxjava 설정. ex) rxjava observe, databinding observe..
    abstract fun initAfterBinding() // 바인딩 이후에 할 일을 여기에 구현. 그 외에 설정할 것이 있으면 이곳에서 설정. 클릭 리스너도 이곳에서 설정.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false);
        val rootView = viewDataBinding.root

        initStartView()
        initDataBinding()
        initAfterBinding()

        return rootView;
    }
}