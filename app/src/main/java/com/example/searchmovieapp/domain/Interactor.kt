package com.example.searchmovieapp.domain

import com.example.searchmovieapp.data.remote.entities.MovieDetailsEntity
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.data.ResultWrapper

interface Interactor {
    fun getNowPlayingTotalPages(): Int
    fun getUpcomingTotalPages(): Int
    fun getTopRatedTotalPages(): Int
    suspend fun forcedUpdate()
    suspend fun getNowPlayingMovies(page: Int): ResultWrapper<List<MovieEntity>>
    suspend fun getUpcomingMovies(page: Int): ResultWrapper<List<MovieEntity>>
    suspend fun getTopRatedMovies(page: Int): ResultWrapper<List<MovieEntity>>
    suspend fun getAllLocalCachedNowPlayingMovies(): List<MovieEntity>
    suspend fun getAllLocalCachedUpcomingMovies(): List<MovieEntity>
    suspend fun getAllLocalCachedTopRatedMovies(): List<MovieEntity>
    suspend fun getMovieDetails(movieId: Int): ResultWrapper<MovieDetailsEntity>
    suspend fun addToFavorites(movie: MovieEntity)
    suspend fun removeFromFavorites(movie: MovieEntity)
    suspend fun getFavoritesMovies(): List<MovieEntity>
}