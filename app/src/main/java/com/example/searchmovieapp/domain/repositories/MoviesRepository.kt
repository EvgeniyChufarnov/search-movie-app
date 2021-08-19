package com.example.searchmovieapp.domain.repositories

import com.example.searchmovieapp.domain.data.ResultWrapper
import com.example.searchmovieapp.domain.data.remote.entities.MovieEntity

interface MoviesRepository {
    var nowPlayingTotalPages: Int
    var upcomingTotalPages: Int
    var topRatedTotalPages: Int
    suspend fun getNowPlayingMovies(page: Int, language: String): ResultWrapper<List<MovieEntity>>
    suspend fun getUpcomingMovies(page: Int, language: String): ResultWrapper<List<MovieEntity>>
    suspend fun getTopRatedMovies(page: Int, language: String): ResultWrapper<List<MovieEntity>>
}