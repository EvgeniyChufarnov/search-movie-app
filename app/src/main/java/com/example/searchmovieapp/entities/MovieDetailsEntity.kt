package com.example.searchmovieapp.entities

data class MovieDetailsEntity (
    val id: Int,
    val title: String,
    val posterPath: String?,
    val originalTitle: String,
    val runtime: Int?,
    val budget: Int,
    val revenue: Int,
    val genres: List<String>,
    val releaseDate: String,
    val voteAverage: Float,
    val voteCount: Int,
    val overview: String?,
)