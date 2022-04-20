package com.jooheon.clean_architecture.data.repository


import com.jooheon.clean_architecture.data.datasource.TempDataSource
import com.jooheon.clean_architecture.data.datasource.WikipediaRemoteDataSource
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.repository.WikipediaRepository

class WikipediaRepositoryImpl(
    private val wikipediaRemoteDataSource: WikipediaRemoteDataSource,
    private val databaseSource: TempDataSource
): WikipediaRepository {

    override suspend fun getDetail(word: String): Resource<String> {
        return wikipediaRemoteDataSource.getDetail(word)
    }

    override suspend fun getSummary(word: String): Resource<Entity.Summary> {
        return wikipediaRemoteDataSource.getSummary(word)
    }

    override suspend fun getRelated(word: String): Resource<Entity.Related> {
        return wikipediaRemoteDataSource.getRelated(word)
    }

    companion object {
        val TAG = WikipediaRepositoryImpl::class.simpleName
    }
}