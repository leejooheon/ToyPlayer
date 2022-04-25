package com.jooheon.clean_architecture.presentation.view.main.wikipedia

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.domain.usecase.wikipedia.WikipediaUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import com.jooheon.clean_architecture.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WikipediaViewModel @Inject constructor(
    private val wikipediaUseCase: WikipediaUseCase
): BaseViewModel() {
    override val TAG: String = WikipediaViewModel::class.java.simpleName

    private val _summaryResponse = mutableStateOf<Entity.Summary?>(null)
    val summaryResponse = _summaryResponse

    private val _relatedResponse = mutableStateOf<Entity.Related?>(null)
    val relatedResponse = _relatedResponse

    val searchWord = MutableStateFlow("")

    fun callRelatedApi() {
        if(searchWord.value.isEmpty()) {
            handleAlertDialogState(UiText.DynamicString("text is empty"))
            return
        }

        wikipediaUseCase
            .getRelated(searchWord.value)
            .onEach { resource ->
                Log.d(TAG, "callRelatedApi: $resource")
                handleResponse(resource)

                if(resource is Resource.Success) {
                    _relatedResponse.value = resource.value
                }

                if(resource is Resource.Failure) {
                    _relatedResponse.value = null
                    searchWord.value = ""
                }

            }.launchIn(viewModelScope)
    }

    fun callSummaryApi() {
        wikipediaUseCase
            .getSummary("blizard")
            .onEach { resource ->
                Log.d(TAG, "result: ${resource}")
                handleResponse(resource)
                if(resource is Resource.Success) {
                    _summaryResponse.value = resource.value
                }
            }.launchIn(viewModelScope)
    }
}