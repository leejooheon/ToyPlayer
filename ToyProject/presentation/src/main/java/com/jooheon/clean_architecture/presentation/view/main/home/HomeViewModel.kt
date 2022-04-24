package com.jooheon.clean_architecture.presentation.view.main.home


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val githubUseCase: GithubUseCase
): BaseViewModel() {
    override val TAG: String = HomeViewModel::class.java.simpleName

    val githubId = MutableStateFlow("")

    private val _repositoryResponse = mutableStateOf<List<Entity.Repository>?>(null)
    val repositoryResponse = _repositoryResponse

    fun callRepositoryApi(githubId: String) {
        githubUseCase.getRepository(githubId)
            .onEach { resource ->
                Log.d(TAG, "result: ${resource}")
                handleResponse(resource)

                if(resource is Resource.Success) {
                    insertDummyImageUrl(resource.value)
                    _repositoryResponse.value = resource.value
                }
            }
            .launchIn(viewModelScope)
    }

    private fun insertDummyImageUrl(resource: List<Entity.Repository>) {
        resource.forEachIndexed { index, repository ->
            val imgIndex:Int = index % Entity.tempImages.size
            resource.get(index).imageUrl = Entity.tempImages.get(imgIndex).imageUrl
        }
    }
}