package com.jooheon.clean_architecture.presentation.view.main.github


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.entity.test.TestImage
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.essential.base.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class GithubViewModel @Inject constructor(
    private val githubUseCase: GithubUseCase
): BaseViewModel() {
    override val TAG: String = GithubViewModel::class.java.simpleName

    val githubId = MutableStateFlow("")

    private val _repositoryList = MutableStateFlow<List<Entity.Repository>>(emptyList())
    val repositoryList = _repositoryList.asStateFlow()

    private val _navigateToGithubDetailScreen = Channel<Entity.Repository>()
    val navigateToGithubDetailScreen = _navigateToGithubDetailScreen.receiveAsFlow()

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
                    _repositoryList.value = resource.value
                }

                if(resource is Resource.Failure) {
                    _repositoryList.value = emptyList()
                    githubId.value = ""
                }
            }
            .launchIn(viewModelScope)
    }

    fun onRepositoryClicked(item: Entity.Repository) = viewModelScope.launch(Dispatchers.Main) {

        val repository = Json.encodeToString(item)
        Log.d(TAG, "repository: $repository")
        _navigateToGithubDetailScreen.send(item)
    }

    private fun insertDummyImageUrl(resource: List<Entity.Repository>) {
        resource.forEachIndexed { index, _ ->
            val imgIndex:Int = index % TestImage.list.size
            resource.get(index).imageUrl = TestImage.list.get(imgIndex).imageUrl
        }
    }
}