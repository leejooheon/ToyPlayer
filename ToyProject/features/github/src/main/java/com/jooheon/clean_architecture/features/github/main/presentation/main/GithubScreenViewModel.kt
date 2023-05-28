package com.jooheon.clean_architecture.features.github.main.presentation.main


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.entity.test.TestImage
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.github.main.model.GithubScreenEvent
import com.jooheon.clean_architecture.features.github.main.model.GithubScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class GithubScreenViewModel @Inject constructor(
    private val githubUseCase: GithubUseCase,
): BaseViewModel() {
    override val TAG: String = GithubScreenViewModel::class.java.simpleName

    var githubState by mutableStateOf(GithubScreenState.default)

    private val _navigateToGithubDetailScreen = Channel<GithubScreenState>()
    val navigateToGithubDetailScreen = _navigateToGithubDetailScreen.receiveAsFlow()

    fun dispatch(event: GithubScreenEvent, state: GithubScreenState) {
        when(event) {
            GithubScreenEvent.GetGithubRepositoryData -> getRepositoryData(state.id)
            GithubScreenEvent.GoToDetailScreen -> goToDetailScreen(state.selectedItem)
        }
    }

    private fun getRepositoryData(id: String?) {
        id ?: return

        if(id.isEmpty()) {
            val content = UiText.DynamicString("text is empty")
            handleAlertDialogState(content)
            return;
        }

        githubUseCase.getRepository(id)
            .onEach { resource ->
                Log.d(TAG, "result: ${resource}")
                handleResponse(resource)

                if(resource is Resource.Success) {
                    insertDummyImageUrl(resource.value)
                    githubState = githubState.copy(
                        id = id,
                        items = resource.value
                    )
                }

                if(resource is Resource.Failure) {
                    githubState = GithubScreenState.default.copy()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun goToDetailScreen(item: Entity.Repository?) = viewModelScope.launch(Dispatchers.Main) {
        item ?: return@launch
        val repository = Json.encodeToString(item)
        Log.d(TAG, "repository: $repository")

        githubState = githubState.copy(selectedItem = item)
        _navigateToGithubDetailScreen.send(githubState)
    }

    private fun insertDummyImageUrl(resource: List<Entity.Repository>) {
        resource.forEachIndexed { index, _ ->
            val imgIndex:Int = index % TestImage.list.size
            resource.get(index).imageUrl = TestImage.list.get(imgIndex).imageUrl
        }
    }
}