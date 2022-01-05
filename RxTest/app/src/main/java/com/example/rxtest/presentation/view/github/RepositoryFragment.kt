package com.example.rxtest.presentation.view.github

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.viewModels

import com.example.rxtest.R
import com.example.rxtest.presentation.base.BaseFragment
import com.example.rxtest.databinding.FragmentRepositoryBinding
import com.jooheon.clean_architecture.domain.common.Resource
import com.example.rxtest.presentation.base.extensions.hideKeyboard
import com.example.rxtest.presentation.base.extensions.textChanges

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RepositoryFragment: BaseFragment<FragmentRepositoryBinding>() {
    private val TAG = RepositoryFragment::class.simpleName

    val viewModel: RepositoryViewModel by viewModels()

    override fun initStartView() {

    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun initDataBinding() {
        // viewModel의 LiveData 참조
        viewModel.repositoryLiveData.observe(this, Observer {
            binding.tvRepository.text = it
        })

        binding.etRepository.textChanges().debounce(1000)
            .onEach {
                if(!it.isNullOrEmpty()) {
                    viewModel.callRepositoryApi(it.toString())
                }
            }.launchIn(lifecycleScope)
    }

    override fun initAfterBinding() {
        lifecycleScope.launchWhenResumed {
            viewModel.repositoryResponse.collect {

                if(it == Resource.Loading) {
                    showLoading()
                    hideKeyboard()
                } else {
                    hideLoading()
                }

                when(it) {
                    is Resource.Success -> {
                        Log.d(TAG, "Success")
                        binding.tvRepository.text = it.value.toString()
                    }
                    is Resource.Failure -> {
                        Log.d(TAG, "Failure ${it.message}, ${it.failureStatus}, ${it.code}")
                        binding.tvRepository.text = "${it.message}, ${it.failureStatus}, ${it.code}"
                    }
                }
            }
        }
    }

    override fun getLayoutId() = R.layout.fragment_repository
}