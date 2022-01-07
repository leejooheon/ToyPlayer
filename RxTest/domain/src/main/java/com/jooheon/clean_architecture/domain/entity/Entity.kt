package com.jooheon.clean_architecture.domain.entity

sealed class Entity {

    data class Repository(
        val name: String,
        val id: String,
        val created_at: String,
        val html_url: String
    ) : Entity()
}