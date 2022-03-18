package com.jooheon.clean_architecture.domain.entity

import java.io.Serializable

sealed class Entity: Serializable {

    data class Repository(
        val name: String,
        val id: String,
        val created_at: String,
        val html_url: String,
        var imageUrl: String = ""
    ) : Entity()

    data class Branch(
        val name: String,
        val commit: Commit,
        val protected: Boolean
    ) : Entity() {
        data class Commit(
            val sha: String,
            val url: String
        ) : Entity()
    }

    data class Commit(
        val sha: String,
        val node_id: String,
        val commit: Info
    ) : Entity() {
        data class Info(
            val message: String
        ) : Entity()
    }

    data class User(
        val refreshToken: String,
        val token: String,
        val tokenExpirationDate: String,
        val userId: String
    ) : Entity()

    data class TempImage(
        val id: Long,
        val name: String,
        val imageUrl: String,
        val price: Long,
        val tagline: String = "",
        val tags: Set<String> = emptySet()
    ) : Entity()

    data class Related(
        val pages: List<Page>
    ): Entity() {
        data class Page(
            val content_urls: ContentUrls?,
            val description: String,
            val dir: String,
            val displaytitle: String?,
            val extract: String?,
            val extract_html: String,
            val index: Int,
            val lang: String,
            val namespace: Namespace?,
            val normalizedtitle: String,
            val ns: Int,
            val originalimage: Originalimage?,
            val pageid: Int,
            val revision: String,
            val thumbnail: Thumbnail?,
            val tid: String,
            val timestamp: String,
            val title: String?,
            val titles: Titles?,
            val type: String,
            val wikibase_item: String
        ): Entity() {
            data class ContentUrls(
                val desktop: Desktop,
                val mobile: Mobile
            ): Entity() {
                data class Desktop(
                    val edit: String,
                    val page: String,
                    val revisions: String,
                    val talk: String
                ): Entity()

                data class Mobile(
                    val edit: String,
                    val page: String,
                    val revisions: String,
                    val talk: String
                ): Entity()
            }

            data class Namespace(
                val id: Int,
                val text: String
            ): Entity()

            data class Originalimage(
                val height: Int,
                val source: String,
                val width: Int
            ): Entity()

            data class Thumbnail(
                val height: Int,
                val source: String,
                val width: Int
            ): Entity()

            data class Titles(
                val canonical: String,
                val display: String,
                val normalized: String
            ): Entity()
        }
    }

    data class Summary(
        val content_urls: ContentUrls,
        val description: String,
        val description_source: String,
        val dir: String,
        val displaytitle: String,
        val extract: String,
        val extract_html: String,
        val lang: String,
        val namespace: Namespace,
        val originalimage: Originalimage,
        val pageid: Int,
        val revision: String,
        val thumbnail: Thumbnail?,
        val tid: String,
        val timestamp: String,
        val title: String,
        val titles: Titles,
        val type: String,
        val wikibase_item: String
    ): Entity() {
        data class ContentUrls(
            val desktop: Desktop,
            val mobile: Mobile
        ): Entity() {
            data class Desktop(
                val edit: String,
                val page: String,
                val revisions: String,
                val talk: String
            ): Entity()

            data class Mobile(
                val edit: String,
                val page: String,
                val revisions: String,
                val talk: String
            ): Entity()
        }

        data class Namespace(
            val id: Int,
            val text: String
        ): Entity()

        data class Originalimage(
            val height: Int,
            val source: String,
            val width: Int
        ): Entity()

        data class Thumbnail(
            val height: Int,
            val source: String,
            val width: Int
        ): Entity()

        data class Titles(
            val canonical: String,
            val display: String,
            val normalized: String
        ): Entity()
    }

    companion object {
        val tempImages: List<Entity.TempImage> = listOf(
            Entity.TempImage(
                id = 1L,
                name = "Cupcake",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/pGM4sjt_BdQ",
                price = 299
            ),
            Entity.TempImage(
                id = 2L,
                name = "Donut",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/Yc5sL-ejk6U",
                price = 299
            ),
            Entity.TempImage(
                id = 3L,
                name = "Eclair",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/-LojFX9NfPY",
                price = 299
            ),
            Entity.TempImage(
                id = 4L,
                name = "Froyo",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/3U2V5WqK1PQ",
                price = 299
            ),
            Entity.TempImage(
                id = 5L,
                name = "Gingerbread",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/Y4YR9OjdIMk",
                price = 499
            ),
            Entity.TempImage(
                id = 6L,
                name = "Honeycomb",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/bELvIg_KZGU",
                price = 299
            ),
            Entity.TempImage(
                id = 7L,
                name = "Ice Cream Sandwich",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/YgYJsFDd4AU",
                price = 1299
            ),
            Entity.TempImage(
                id = 8L,
                name = "Jellybean",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/0u_vbeOkMpk",
                price = 299
            ),
            Entity.TempImage(
                id = 9L,
                name = "KitKat",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/yb16pT5F_jE",
                price = 549
            ),
            Entity.TempImage(
                id = 10L,
                name = "Lollipop",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/AHF_ZktTL6Q",
                price = 299
            ),
            Entity.TempImage(
                id = 11L,
                name = "Marshmallow",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/rqFm0IgMVYY",
                price = 299
            ),
            Entity.TempImage(
                id = 12L,
                name = "Nougat",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/qRE_OpbVPR8",
                price = 299
            ),
            Entity.TempImage(
                id = 13L,
                name = "Oreo",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/33fWPnyN6tU",
                price = 299
            ),
            Entity.TempImage(
                id = 14L,
                name = "Pie",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/aX_ljOOyWJY",
                price = 299
            ),
            Entity.TempImage(
                id = 15L,
                name = "Chips",
                imageUrl = "https://source.unsplash.com/UsSdMZ78Q3E",
                price = 299
            ),
            Entity.TempImage(
                id = 16L,
                name = "Pretzels",
                imageUrl = "https://source.unsplash.com/7meCnGCJ5Ms",
                price = 299
            ),
            Entity.TempImage(
                id = 17L,
                name = "Smoothies",
                imageUrl = "https://source.unsplash.com/m741tj4Cz7M",
                price = 299
            ),
            Entity.TempImage(
                id = 18L,
                name = "Popcorn",
                imageUrl = "https://source.unsplash.com/iuwMdNq0-s4",
                price = 299
            ),
            Entity.TempImage(
                id = 19L,
                name = "Almonds",
                imageUrl = "https://source.unsplash.com/qgWWQU1SzqM",
                price = 299
            ),
            Entity.TempImage(
                id = 20L,
                name = "Cheese",
                imageUrl = "https://source.unsplash.com/9MzCd76xLGk",
                price = 299
            ),
            Entity.TempImage(
                id = 21L,
                name = "Apples",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/1d9xXWMtQzQ",
                price = 299
            ),
            Entity.TempImage(
                id = 22L,
                name = "Apple sauce",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/wZxpOw84QTU",
                price = 299
            ),
            Entity.TempImage(
                id = 23L,
                name = "Apple chips",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/okzeRxm_GPo",
                price = 299
            ),
            Entity.TempImage(
                id = 24L,
                name = "Apple juice",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/l7imGdupuhU",
                price = 299
            ),
            Entity.TempImage(
                id = 25L,
                name = "Apple pie",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/bkXzABDt08Q",
                price = 299
            ),
            Entity.TempImage(
                id = 26L,
                name = "Grapes",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/y2MeW00BdBo",
                price = 299
            ),
            Entity.TempImage(
                id = 27L,
                name = "Kiwi",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/1oMGgHn-M8k",
                price = 299
            ),
            Entity.TempImage(
                id = 28L,
                name = "Mango",
                tagline = "A tag line",
                imageUrl = "https://source.unsplash.com/TIGDsyy0TK4",
                price = 299
            )
        )
    }
}