package com.example.rxtest.main

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.MutableLiveData
import com.example.rxtest.R
import com.example.rxtest.api.github.GithubClient
import com.example.rxtest.base.BaseViewModel
import com.example.rxtest.compose.ComposeFragment
import com.example.rxtest.compose.RepositoryFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers;

class MainViewModel : BaseViewModel(){
    private val TAG = MainViewModel::class.simpleName

    init {
        // do something..
    }

    fun replaceComposeFragment(supportFragmentManager: FragmentManager) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<ComposeFragment>(R.id.fragment_container_view)
        }
    }

    fun replaceRepositoryFragment(supportFragmentManager: FragmentManager) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<RepositoryFragment>(R.id.fragment_container_view)
        }
    }
}