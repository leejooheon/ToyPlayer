package com.jooheon.clean_architecture.features.github.main.presentation.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.github.main.model.GithubDetailScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class GithubDetailScreenViewModel @Inject constructor(
    private val githubUseCase: GithubUseCase
): BaseViewModel() {
    override val TAG: String = GithubDetailScreenViewModel::class.java.simpleName

    var state by mutableStateOf(GithubDetailScreenState.default)
        private set

    fun initState(id: String, item: Entity.Repository) {
        state = state.copy(
            id = id,
            item = item
        )
        callCommitAndBranchApi()
    }

    private fun callCommitAndBranchApi() {
        githubUseCase.getBranchAndCommit(state.id, state.item.name)
            .onEach { resources ->
                val branchResponse = resources.first
                val commitResponse = resources.second

                handleResponse(branchResponse)
                handleResponse(commitResponse)

                listOf(branchResponse, commitResponse)
                    .fastFirstOrNull {
                        it is Resource.Loading
                    }?.let {
                        return@onEach
                    }

                val commitList = if (commitResponse is Resource.Success) {
                    Log.d(TAG, "commitResponse: ${commitResponse.value}")
                    commitResponse.value
                } else {
                    emptyList()
                }

                val branchList = if (branchResponse is Resource.Success) {
                    Log.d(TAG, "branchResponse: ${branchResponse.value}")
                    branchResponse.value
                } else {
                    emptyList()
                }

                state = state.copy(
                    commitList = commitList,
                    branchList = branchList
                )
            }.launchIn(viewModelScope)
    }
}