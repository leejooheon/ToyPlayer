package com.jooheon.clean_architecture.domain.entity

import kotlinx.serialization.Serializable

@Serializable
sealed class Entity: java.io.Serializable {
    enum class SupportLaunguages(val code: String) {
        AUTO("Auto"),
        ENGLISH("en"),
        KOREAN("ko");
    }

    enum class SupportThemes(val code: String) {
        AUTO("auto"),
        DARK("dark"),
        LIGHT("light"),
        DYNAMIC_DARK("dynamic_dark"),
        DYNAMIC_LIGHT("dynamic_light");
    }

    data class ParkingSpot(
        val lat: Double,
        val lng: Double,
        val id: Int?
    ) : Entity()

    @Serializable
    data class Repository(
        val name: String,
        val id: String,
        val created_at: String,
        val html_url: String,
        var imageUrl: String = ""
    ) : Entity() {
        companion object {
            val default = Repository(
                name = "name",
                id = "id",
                created_at = "created_at",
                html_url = "https://asd.com",
                imageUrl = "image"
            )
        }
    }

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

    @Serializable
    data class Related(
        val pages: List<Page>
    ) : Entity() {
        @Serializable
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
        ) : Entity() {
            companion object {
                val default = Page(
                    content_urls = null,
                    description = "description",
                    dir = "dir",
                    displaytitle = "display_title",
                    extract = "extract",
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
            }
            @Serializable
            data class ContentUrls(
                val desktop: Desktop,
                val mobile: Mobile
            ) : Entity() {
                @Serializable
                data class Desktop(
                    val edit: String,
                    val page: String,
                    val revisions: String,
                    val talk: String
                ) : Entity()

                @Serializable
                data class Mobile(
                    val edit: String,
                    val page: String,
                    val revisions: String,
                    val talk: String
                ) : Entity()
            }

            @Serializable
            data class Namespace(
                val id: Int,
                val text: String
            ) : Entity()

            @Serializable
            data class Originalimage(
                val height: Int,
                val source: String,
                val width: Int
            ) : Entity()

            @Serializable
            data class Thumbnail(
                val height: Int,
                val source: String,
                val width: Int
            ) : Entity()

            @Serializable
            data class Titles(
                val canonical: String,
                val display: String,
                val normalized: String
            ) : Entity()
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
    ) : Entity() {
        data class ContentUrls(
            val desktop: Desktop,
            val mobile: Mobile
        ) : Entity() {
            data class Desktop(
                val edit: String,
                val page: String,
                val revisions: String,
                val talk: String
            ) : Entity()

            data class Mobile(
                val edit: String,
                val page: String,
                val revisions: String,
                val talk: String
            ) : Entity()
        }

        data class Namespace(
            val id: Int,
            val text: String
        ) : Entity()

        data class Originalimage(
            val height: Int,
            val source: String,
            val width: Int
        ) : Entity()

        data class Thumbnail(
            val height: Int,
            val source: String,
            val width: Int
        ) : Entity()

        data class Titles(
            val canonical: String,
            val display: String,
            val normalized: String
        ) : Entity()
    }

    data class Station(
        val errorMessage: ErrorMessage,
        val realtimeArrivalList: List<RealtimeArrival>
    ) : Entity() {
        data class ErrorMessage(
            val code: String?,
            val developerMessage: String?,
            val link: String?,
            val message: String?,
            val status: Int?,
            val total: Int?
        ) : Entity()

        data class RealtimeArrival(
            val arvlCd: String?,
            val arvlMsg2: String?,
            val arvlMsg3: String?,
            val barvlDt: String?,
            val bstatnId: String?,
            val bstatnNm: String?,
            val btrainNo: String?,
            val ordkey: String?,
            val recptnDt: String?,
            val rowNum: Int?,
            val selectedCount: Int?,
            val statnFid: String?,
            val statnId: String?,
            val statnList: String?,
            val statnNm: String?,
            val statnTid: String?,
            val subwayHeading: String?,
            val subwayId: String?,
            val subwayList: String?,
            val totalCount: Int?,
            val trainLineNm: String?,
            val updnLine: String?
        ) : Entity()

        companion object {
            val DEFAULT_VALUE = Station(
                errorMessage = ErrorMessage(
                    code = null,
                    developerMessage = null,
                    link = null,
                    message = null,
                    status = null,
                    total = null
                ),
                realtimeArrivalList = listOf(
                    RealtimeArrival(
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null,
                        null, null, null, null, null
                    )
                )
            )
        }
    }
}