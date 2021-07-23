package com.example.searchmovieapp.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDetailsEntity (
    val id: Int,
    val title: String,
    @Json(name = "poster_path")
    var posterPath: String?,
    @Json(name = "original_title")
    val originalTitle: String,
    val runtime: Int?,
    val budget: Int,
    val revenue: Int,
    @Json(name = "genres")
    val genresEntities: List<GenreEntity>,
    @Json(name = "release_date")
    val releaseDate: String,
    @Json(name = "vote_average")
    val voteAverage: Float,
    @Json(name = "vote_count")
    val voteCount: Int,
    val overview: String?,
) {
    val genres = genresEntities.map { it.name }
}

@JsonClass(generateAdapter = true)
data class GenreEntity (
    val id: Int,
    val name: String
)