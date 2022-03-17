package com.jooheon.clean_architecture.presentation.view.temp

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.wikipedia.WikipediaUseCase
import kotlinx.coroutines.flow.Flow

class EmptyWikipediaUseCase: WikipediaUseCase {
    override fun getDetail(word: String): Flow<Resource<String>> {
        TODO("Not yet implemented")
    }

    override fun getSummary(word: String): Flow<Resource<Entity.Summary>> {
        TODO("Not yet implemented")
    }

    override fun getRelated(word: String): Flow<Resource<Entity.Related>> {
        TODO("Not yet implemented")
    }
}