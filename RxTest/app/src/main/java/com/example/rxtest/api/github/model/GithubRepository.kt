package com.example.rxtest.api.github.model

import com.google.gson.annotations.SerializedName

data class GithubRepository(
    @SerializedName("name") val name: String,
    @SerializedName("id") val id: String,
    @SerializedName("created_at") val date: String,
    @SerializedName("html_url") val url: String
) {
    override fun toString(): String = name + id + date + url + "\n";
}