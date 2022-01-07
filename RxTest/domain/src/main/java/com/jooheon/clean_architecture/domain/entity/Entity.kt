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
}