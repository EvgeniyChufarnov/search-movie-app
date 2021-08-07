package com.example.searchmovieapp.domain.repositories

import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.data.remote.entities.MovieDetailsEntity

interface MovieDetailsRepository {
    suspend fun getMovieDetails(movieId: Int, language: String): ResultWrapper<MovieDetailsEntity>
}