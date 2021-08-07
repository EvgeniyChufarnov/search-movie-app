package com.example.searchmovieapp.data.remote.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDetailsEntity(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "poster_path") var posterPath: String?,
    @Json(name = "original_title") val originalTitle: String,
    @Json(name = "runtime") val runtime: Int?,
    @Json(name = "budget") val budget: Int,
    @Json(name = "revenue") val revenue: Int,
    @Json(name = "genres") val genresEntities: List<GenreEntity>,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "vote_average") val voteAverage: Float,
    @Json(name = "vote_count") val voteCount: Int,
    @Json(name = "overview") val overview: String?,
) {
    val genres = genresEntities.map { it.name }
    var isFavorite = false
}

@JsonClass(generateAdapter = true)
data class GenreEntity(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)