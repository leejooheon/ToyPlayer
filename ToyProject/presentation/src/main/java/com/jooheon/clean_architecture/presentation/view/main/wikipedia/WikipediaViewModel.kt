package com.jooheon.clean_architecture.presentation.view.main.wikipedia

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.wikipedia.WikipediaUseCase
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.essential.base.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WikipediaViewModel @Inject constructor(
    private val wikipediaUseCase: WikipediaUseCase
): BaseViewModel() {
    override val TAG: String = WikipediaViewModel::class.java.simpleName

    private val _summaryResponse = MutableStateFlow<Entity.Summary?>(null)
    val summaryResponse = _summaryResponse.asStateFlow()

    private val _relatedResponse = MutableStateFlow<Entity.Related?>(null)
    val relatedResponse = _relatedResponse.asStateFlow()

    private val _navigateToWikipediaDetailScreen = Channel<Entity.Related.Page>()
    val navigateToWikipediaDetailScreen = _navigateToWikipediaDetailScreen.receiveAsFlow()

    val searchWord = MutableStateFlow("")

    fun onRelatedItemClicked(page: Entity.Related.Page) = viewModelScope.launch(Dispatchers.Main) {
        _navigateToWikipediaDetailScreen.send(page)
    }

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