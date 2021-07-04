package com.example.searchmovieapp.entities

data class MovieEntity(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String,
    val voteAverage: Float,
    val isUpcoming: Boolean
)