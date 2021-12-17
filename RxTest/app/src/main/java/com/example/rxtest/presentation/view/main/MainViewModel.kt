package com.example.rxtest.presentation.view.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.rxtest.R
import com.example.rxtest.data.datasource.GithubApiDataSource
import com.example.rxtest.domain.usecase.github.GithubUseCase
import com.example.rxtest.presentation.base.BaseViewModel
import com.example.rxtest.presentation.view.temp.compose.ComposeFragment
import com.example.rxtest.presentation.view.temp.compose.RepositoryFragment
import javax.inject.Inject

class MainViewModel @Inject constructor(private val githubUseCase: GithubUseCase): BaseViewModel() {
    private val TAG = MainViewModel::class.simpleName

    @ExperimentalFoundationApi
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