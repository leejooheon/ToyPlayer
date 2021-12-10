package com.example.rxtest.domain.usecase.github

import com.example.rxtest.domain.common.ResultState
import com.example.rxtest.domain.entity.Entity
import com.example.rxtest.domain.repository.GithubRepository
import io.reactivex.Single
import ir.hosseinabbasi.domain.common.transformer.STransformer

class GithubUseCaseImpl(
    private val transformerSingleListRepo: STransformer<ResultState<List<Entity.Repository>>>,
    private val transformerSingleListProjects: STransformer<ResultState<List<Entity.Projects>>>, // TODO: 뭐지??
    private val repository: GithubRepository
): GithubUseCase {
    override fun getRepository(owner: String): Single<ResultState<List<Entity.Repository>>> =
        repository.getRepository(owner).compose(transformerSingleListRepo)

    override fun getProjects(owner: String): Single<ResultState<List<Entity.Projects>>> =
        repository.getProjects(owner).compose(transformerSingleListProjects)
}