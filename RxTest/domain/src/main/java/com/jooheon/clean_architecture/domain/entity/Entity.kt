package com.jooheon.clean_architecture.domain.entity

sealed class Entity {

    data class Repository(
        val name: String,
        val id: String,
        val created_at: String,
        val html_url: String
    ) : Entity()

    data class Branch(
        val name: String,
        val commit: Commit,
        val protected: Boolean
    ) : Entity() {
        data class Commit(
            val sha: String,
            val url: String
        )
    }

    data class Commit(
        val sha: String,
        val node_id: String
    )

    data class User(
        val refreshToken: String,
        val token: String,
        val tokenExpirationDate: String,
        val userId: String
    )

    data class TempImage(
        val id: Long,
        val name: String,
        val imageUrl: String,
        val price: Long,
        val tagline: String = "",
        val tags: Set<String> = emptySet()
    )
}