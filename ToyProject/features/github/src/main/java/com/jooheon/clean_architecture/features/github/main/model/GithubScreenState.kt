package com.jooheon.clean_architecture.features.github.main.model

import com.jooheon.clean_architecture.domain.entity.Entity
data class GithubScreenState(
    val id: String,
    val items: List<Entity.Repository>,
    val selectedItem: Entity.Repository?
) {
    companion object {
        val default = GithubScreenState(
            id = "",
            items = emptyList(),
            selectedItem = null
        )
    }
}