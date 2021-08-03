package com.example.searchmovieapp.repositories

import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.entities.MovieDetailsEntity

interface MovieDetailsRepository {
    suspend fun getMovieDetails(movieId: Int, language: String): ResultWrapper<MovieDetailsEntity>
}