package com.example.searchmovieapp.domain.repositories

import com.example.searchmovieapp.domain.data.ResultWrapper
import com.example.searchmovieapp.domain.data.remote.entities.MovieEntity

interface LocalMoviesRepository {
    suspend fun getNowPlayingMovies(page: Int, language: String): ResultWrapper<List<MovieEntity>>
    suspend fun getUpcomingMovies(page: Int, language: String): ResultWrapper<List<MovieEntity>>
    suspend fun getTopRatedMovies(page: Int, language: String): ResultWrapper<List<MovieEntity>>
    suspend fun getAllLocalCachedNowPlayingMovies(language: String): List<MovieEntity>
    suspend fun getAllLocalCachedUpcomingMovies(language: String): List<MovieEntity>
    suspend fun getAllLocalCachedTopRatedMovies(language: String): List<MovieEntity>
    suspend fun cacheNowPlayingMovies(page: Int, language: String, movies: List<MovieEntity>)
    suspend fun cacheUpcomingMovies(page: Int, language: String, movies: List<MovieEntity>)
    suspend fun cacheTopRatedMovies(page: Int, language: String, movies: List<MovieEntity>)
    suspend fun clear()
}