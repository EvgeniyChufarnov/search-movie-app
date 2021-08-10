package com.example.searchmovieapp.domain.interactors

import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.data.ResultWrapper

interface MoviesInteractor {
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
}