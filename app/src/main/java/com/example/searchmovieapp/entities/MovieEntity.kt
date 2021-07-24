package com.example.searchmovieapp.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieEntity(
    val id: Int,
    val title: String,
    @Json(name = "poster_path")
    var posterPath: String?,
    @Json(name = "release_date")
    val releaseDate: String,
    @Json(name = "vote_average")
    val voteAverage: Float,
) {
    var isUpcoming: Boolean = true
}