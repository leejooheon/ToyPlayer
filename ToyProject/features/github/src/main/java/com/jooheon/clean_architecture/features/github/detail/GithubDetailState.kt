package com.jooheon.clean_architecture.features.github.detail

import androidx.compose.runtime.mutableStateOf
import com.jooheon.clean_architecture.domain.entity.Entity

data class GithubDetailState(
    val id: String,
    val item: Entity.Repository,
    val commitList: List<Entity.Commit>,
    val branchList: List<Entity.Branch>,
) {
    companion object {
        val default = GithubDetailState(
            id = "",
            item = Entity.Repository.default,
            commitList = emptyList(),
            branchList = emptyList(),
        )
    }
}