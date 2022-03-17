package com.jooheon.clean_architecture.domain.usecase.wikipedia

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow

interface WikipediaUseCase: BaseUseCase {
    fun getDetail(word: String): Flow<Resource<String>>
    fun getSummary(word: String): Flow<Resource<Entity.Summary>>
    fun getRelated(word: String): Flow<Resource<Entity.Related>>
}