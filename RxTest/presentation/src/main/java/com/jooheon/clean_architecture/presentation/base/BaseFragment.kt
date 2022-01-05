package com.jooheon.clean_architecture.presentation.base

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.jooheon.clean_architecture.presentation.utils.hideLoadingDialog
import com.jooheon.clean_architecture.presentation.utils.showLoadingDialog

abstract class BaseFragment<VB : ViewDataBinding>: Fragment() {

    private var mProgressDialog: Dialog? = null

    private lateinit var viewDataBinding: VB
    open val binding get() = viewDataBinding

    private var mRootView: View? = null
    private var hasInitializedRootView = false

    abstract fun initStartView() // 레이아웃을 띄운 직후 호출. 뷰나 액티비티의 속성 등을 초기화. ex) 리사이클러뷰, 툴바, 드로어뷰..
    abstract fun initDataBinding() // 두번째로 호출. 데이터 바인딩 및 rxjava 설정. ex) rxjava observe, databinding observe..
    abstract fun initAfterBinding() // 바인딩 이후에 할 일을 여기에 구현. 그 외에 설정할 것이 있으면 이곳에서 설정. 클릭 리스너도 이곳에서 설정.

    open fun registerListeners() {}
    open fun unRegisterListeners() {}
    open fun getFragmentArguments() {}
    open fun setBindingVariables() {}
    open fun setUpViews() {}
    open fun observeAPICall() {}
    open fun setupObservers() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(mRootView == null) {
            initViewBinding(inflater, container)
        }

        initStartView()
        initDataBinding()
        initAfterBinding()

        return mRootView
    }

    private fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)

        mRootView = viewDataBinding.root
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.executePendingBindings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        if (!hasInitializedRootView) {
            getFragmentArguments()
            setBindingVariables()
            setUpViews()
            observeAPICall()
            setupObservers()

            hasInitializedRootView = true
        }
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    fun showLoading() {
        hideLoading()
        mProgressDialog = showLoadingDialog(requireActivity())
    }
    fun hideLoading() = hideLoadingDialog(mProgressDialog, requireActivity())

    companion object {
        val TAG = "BaseFragment"
    }
}