package com.example.rxtest.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.example.rxtest.R
import com.example.rxtest.base.BaseFragment
import com.example.rxtest.databinding.FragmentRepositoryBinding
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.schedulers.Schedulers

import java.util.concurrent.TimeUnit

class RepositoryFragment: BaseFragment<FragmentRepositoryBinding, RepositoryViewModel>() {
    private val TAG = RepositoryViewModel::class.simpleName

    override val layoutResourceId: Int
        get() = R.layout.fragment_repository
    override val viewModel: RepositoryViewModel = RepositoryViewModel()

    override fun initStartView() {

    }

    override fun initDataBinding() {
        // viewModel의 LiveData 참조
        viewModel.repositoryLiveData.observe(this, Observer {
            viewDataBinding.tvRepository.setText(it);
        })

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}