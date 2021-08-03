package com.example.searchmovieapp.repositories

import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.entities.MovieEntity

interface MoviesRepository {
    var nowPlayingTotalPages: Int
    var upcomingTotalPages: Int
    var topRatedTotalPages: Int
    suspend fun getNowPlayingMovies(page: Int, language: String): ResultWrapper<List<MovieEntity>>
    suspend fun getUpcomingMovies(page: Int, language: String): ResultWrapper<List<MovieEntity>>
    suspend fun getTopRatedMovies(page: Int, language: String): ResultWrapper<List<MovieEntity>>
    suspend fun getAllLocalCachedNowPlayingMovies(language: String): List<MovieEntity>
    suspend fun getAllLocalCachedUpcomingMovies(language: String): List<MovieEntity>
    suspend fun getAllLocalCachedTopRatedMovies(language: String): List<MovieEntity>
}