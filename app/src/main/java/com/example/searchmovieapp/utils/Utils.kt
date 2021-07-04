package com.example.searchmovieapp.utils

import com.example.searchmovieapp.entities.MovieEntity

private const val INDEX_OF_LAST_YEAR_CHAR = 3

val MovieEntity.releaseYear: String
    get() = releaseDate.slice(0..INDEX_OF_LAST_YEAR_CHAR)