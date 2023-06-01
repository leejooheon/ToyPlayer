package com.jooheon.clean_architecture.features.github.main.model

import com.jooheon.clean_architecture.domain.entity.Entity

data class GithubDetailScreenState(
    val id: String,
    val item: Entity.Repository,
    val commitList: List<Entity.Commit>,
    val branchList: List<Entity.Branch>,
) {
    companion object {
        val default = GithubDetailScreenState(
            id = "",
            item = Entity.Repository.default,
            commitList = emptyList(),
            branchList = emptyList(),
        )
    }
}