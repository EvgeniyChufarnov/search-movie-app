package com.example.searchmovieapp.data.remote.entities

import com.example.searchmovieapp.domain.data.remote.entities.MovieEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoviesResultEntity(
    @Json(name = "page") var page: Int,
    @Json(name = "results") var results: List<MovieEntity>,
    @Json(name = "total_results") var totalResults: Int,
    @Json(name = "total_pages") var totalPages: Int
)