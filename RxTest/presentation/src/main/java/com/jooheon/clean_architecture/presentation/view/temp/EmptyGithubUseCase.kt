package com.jooheon.clean_architecture.presentation.view.temp

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import kotlinx.coroutines.flow.Flow

class EmptyGithubUseCase: GithubUseCase {

    override fun getRepository(owner: String): Flow<Resource<List<Entity.Repository>>> {
        TODO("Not yet implemented")
    }

    override fun getBranch(owner: String, repository: String): Flow<Resource<List<Entity.Branch>>> {
        TODO("Not yet implemented")
    }

    override fun getCommit(owner: String, repository: String): Flow<Resource<List<Entity.Commit>>> {
        TODO("Not yet implemented")
    }

    override fun getBranchAndCommit(
        owner: String,
        repository: String
    ): Flow<Pair<Resource<List<Entity.Branch>>, Resource<List<Entity.Commit>>>> {
        TODO("Not yet implemented")
    }

    companion object {
        fun repositoryDummyData():List<Entity.Repository> {
            val items = ArrayList<Entity.Repository>()

            for(i in 0..10) {
                val item = Entity.Repository(
                    name = "name $i",
                    id = "id $i",
                    created_at = "created_at",
                    html_url = "html_url",
                    imageUrl = Entity.tempImages.get(i).imageUrl
                )

                items.add(item)
            }

            return items
        }
    }
}