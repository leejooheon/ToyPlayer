package com.jooheon.clean_architecture.presentation.view.temp

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.wikipedia.WikipediaUseCase
import kotlinx.coroutines.flow.Flow

class EmptyWikipediaUseCase: WikipediaUseCase {
    override fun getDetail(word: String): Flow<Resource<String>> {
        TODO("Not yet implemented")
    }

    override fun getSummary(word: String): Flow<Resource<Entity.Summary>> {
        TODO("Not yet implemented")
    }

    override fun getRelated(word: String): Flow<Resource<Entity.Related>> {
        TODO("Not yet implemented")
    }

    companion object {
        fun dummyData(index: Int): Entity.Related.Page {
            val page = Entity.Related.Page(
                content_urls = null,
                description = "description",
                dir = "dir",
                displaytitle = "display_title - $index",
                extract = "extract - $index",
                extract_html = "ext",
                index = 0,
                lang = "lang",
                namespace = null,
                normalizedtitle = "",
                ns = 0,
                originalimage = null,
                pageid = 0,
                revision = "",
                thumbnail = null,
                tid = "",
                timestamp = "",
                title = "",
                titles = null,
                type = "",
                wikibase_item = ""
            )
            return page
        }
    }
}