package com.jooheon.clean_architecture.features.wikipedia.model

import com.jooheon.clean_architecture.domain.entity.Entity

data class WikipediaScreenState(
    val searchWord: String,
    val relatedItems: List<Entity.Related.Page>,
    val summaryItem: Entity.Summary?,
) {
    companion object {
        val default = WikipediaScreenState(
            searchWord = "",
            relatedItems = emptyList(),
            summaryItem = null,
        )
    }
}