package com.jooheon.clean_architecture.presentation.base

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.jooheon.clean_architecture.presentation.utils.hideLoadingDialog
import com.jooheon.clean_architecture.presentation.utils.showLoadingDialog

abstract class BaseComposeFragment: Fragment() {
    private var mProgressDialog: Dialog? = null
    private var hasInitializedRootView: Boolean = false

    open fun registerListeners() {}
    open fun unRegisterListeners() {}
    open fun getFragmentArguments() {}
    open fun setBindingVariables() {}
    open fun setUpViews() {}
    open fun observeAPICall() {}
    open fun setupObservers() {}



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasInitializedRootView) {
            getFragmentArguments()
            setBindingVariables()
            setUpViews()
            observeAPICall()
            setupObservers()

            hasInitializedRootView = true
        }
    }

    fun showLoading() {
        hideLoading()
        mProgressDialog = showLoadingDialog(requireActivity())
    }
    fun hideLoading() = hideLoadingDialog(mProgressDialog, requireActivity())
}