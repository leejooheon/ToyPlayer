package com.jooheon.clean_architecture.features.wikipedia.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.wikipedia.WikipediaUseCase
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.wikipedia.model.WikipediaScreenEvent
import com.jooheon.clean_architecture.features.wikipedia.model.WikipediaScreenState
import com.jooheon.clean_architecture.toyproject.features.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WikipediaScreenViewModel @Inject constructor(
    private val wikipediaUseCase: WikipediaUseCase
): BaseViewModel() {
    override val TAG: String = WikipediaScreenViewModel::class.java.simpleName

    var state by mutableStateOf(WikipediaScreenState.default)
        private set

    private val _navigateToWikipediaDetailScreen = Channel<Entity.Related.Page>()
    val navigateToWikipediaDetailScreen = _navigateToWikipediaDetailScreen.receiveAsFlow()

    fun dispatch(event: WikipediaScreenEvent) = viewModelScope.launch {
        when(event) {
            is WikipediaScreenEvent.OnSearchButtonClick -> {
                getRelatedPageData(event.searchWord)
                getSummaryData(event.searchWord)
            }
            is WikipediaScreenEvent.OnRelatedPageItemClick -> _navigateToWikipediaDetailScreen.send(event.item)
        }
    }

    private fun getRelatedPageData(searchWord: String) {
        if(searchWord.isEmpty()) {
            handleAlertDialogState(UiText.DynamicString("text is empty"))
            return
        }

        wikipediaUseCase
            .getRelated(searchWord)
            .onEach { resource ->
                Log.d(TAG, "callRelatedApi: $resource")
                handleResponse(resource)

                if(resource is Resource.Success) {
                    state = state.copy(
                        searchWord = searchWord,
                        relatedItems = resource.value.pages
                    )
                }

                if(resource is Resource.Failure) {
                    state = state.copy(
                        searchWord = "",
                        relatedItems = emptyList()
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun getSummaryData(searchWord: String) {
        if(searchWord.isEmpty()) {
            handleAlertDialogState(UiText.DynamicString("text is empty"))
            return
        }

        wikipediaUseCase
            .getSummary(searchWord)
            .onEach { resource ->
                Log.d(TAG, "result: ${resource}")
                handleResponse(resource)


                if(resource is Resource.Success) {
                    state = state.copy(
                        searchWord = searchWord,
                        summaryItem = resource.value,
                    )
                }

                if(resource is Resource.Failure) {
                    state = state.copy(
                        searchWord = "",
                        summaryItem = null,
                    )
                }
            }.launchIn(viewModelScope)
    }
}