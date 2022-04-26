package com.jooheon.clean_architecture.presentation.view.main.github


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import com.jooheon.clean_architecture.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class GithubViewModel @Inject constructor(
    private val githubUseCase: GithubUseCase
): BaseViewModel() {
    override val TAG: String = GithubViewModel::class.java.simpleName

    val githubId = MutableStateFlow("")

    private val _repositoryResponse = mutableStateOf<List<Entity.Repository>?>(null)
    val repositoryResponse = _repositoryResponse

    fun callRepositoryApi() {
        if(githubId.value.isEmpty()) {
            val content = UiText.DynamicString("text is empty")
            handleAlertDialogState(content)
            return;
        }

        githubUseCase.getRepository(githubId.value)
            .onEach { resource ->
                Log.d(TAG, "result: ${resource}")
                handleResponse(resource)

                if(resource is Resource.Success) {
                    insertDummyImageUrl(resource.value)
                    _repositoryResponse.value = resource.value
                }

                if(resource is Resource.Failure) {
                    _repositoryResponse.value = null
                    githubId.value = ""
                }
            }
            .launchIn(viewModelScope)
    }

    private fun insertDummyImageUrl(resource: List<Entity.Repository>) {
        resource.forEachIndexed { index, _ ->
            val imgIndex:Int = index % Entity.tempImages.size
            resource.get(index).imageUrl = Entity.tempImages.get(imgIndex).imageUrl
        }
    }
}