package com.example.rxtest.data.mapper

import com.example.rxtest.data.api.GithubApi
import com.example.rxtest.domain.entity.Entity
import com.google.gson.annotations.SerializedName

fun GithubApi.Dto.Repository.map() = Entity.Repository(
    name = name,
    id = id,
    date = date,
    url = url
)

fun GithubApi.Dto.Projects.map() = Entity.Projects(
    accept = accept,
    org = org,
    state = state,
    perPage = perPage,
    page = page
)