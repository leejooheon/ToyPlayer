package com.example.rxtest.main

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.rxtest.R
import com.example.rxtest.base.BaseViewModel
import com.example.rxtest.compose.ComposeFragment
import com.example.rxtest.compose.RepositoryFragment

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