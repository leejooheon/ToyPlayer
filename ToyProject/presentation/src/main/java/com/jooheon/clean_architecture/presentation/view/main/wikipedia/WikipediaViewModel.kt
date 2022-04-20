package com.jooheon.clean_architecture.presentation.view.main.wikipedia

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.domain.usecase.wikipedia.WikipediaUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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

    fun callRelatedApi(word: String) {
        if(word.isEmpty()) {
            handleAlertDialogState("text is empty")
            return
        }

        wikipediaUseCase
            .getRelated(word)
            .onEach { resource ->
                Log.d(TAG, "callRelatedApi: $resource")
                handleResponse(resource)

                if(resource is Resource.Success) {
                    _relatedResponse.value = resource.value
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