package com.example.searchmovieapp.utils

import com.example.searchmovieapp.entities.MovieEntity

val MovieEntity.releaseYear: String
    get() = releaseDate.slice(0..3)