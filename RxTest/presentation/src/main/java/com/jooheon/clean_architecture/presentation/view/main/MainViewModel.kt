package com.jooheon.clean_architecture.presentation.view.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.viewModelScope

import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import com.jooheon.clean_architecture.presentation.view.github.RepositoryFragment
import dagger.hilt.android.lifecycle.HiltViewModel

import com.jooheon.clean_architecture.presentation.view.temp.compose.ComposeFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
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

    fun replaceHomeFragment(supportFragmentManager: FragmentManager) {

    }
}