package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity

interface WikipediaRepository {
    suspend fun getDetail(word: String): Resource<String>
    suspend fun getSummary(word: String): Resource<Entity.Summary>
    suspend fun getRelated(word: String): Resource<Entity.Related>
}