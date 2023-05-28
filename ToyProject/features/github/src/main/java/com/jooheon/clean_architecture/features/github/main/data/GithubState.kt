package com.jooheon.clean_architecture.features.github.main.data

import com.jooheon.clean_architecture.domain.entity.Entity
data class GithubState(
    val id: String,
    val items: List<Entity.Repository>,
    val selectedItem: Entity.Repository?
) {
    companion object {
        val default = GithubState(
            id = "",
            items = emptyList(),
            selectedItem = null
        )
    }
}