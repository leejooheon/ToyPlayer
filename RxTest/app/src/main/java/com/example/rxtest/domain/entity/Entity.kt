package com.example.rxtest.domain.entity

import com.google.gson.annotations.SerializedName

sealed class Entity {

    data class Repository(
        val name: String,
        val id: String,
        val created_at: String,
        val html_url: String
    ) : Entity()

    data class Projects(
        val accept: String,
        val org: String,
        val state: String,
        val perPage: String,
        val page: String
    ) : Entity()
}