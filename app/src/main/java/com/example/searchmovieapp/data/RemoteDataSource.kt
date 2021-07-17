package com.example.searchmovieapp.data

import com.example.searchmovieapp.entities.MovieDetailsEntity
import com.example.searchmovieapp.entities.MovieEntity

interface RemoteDataSource {
    suspend fun getNowPlayingMovies(): List<MovieEntity>
    suspend fun getUpcomingMovies(): List<MovieEntity>
    suspend fun getFavoritesMovies(): List<MovieEntity>
    suspend fun getTopRatedMovies(): List<MovieEntity>
    suspend fun getMovieDetailsById(movieId: Int): MovieDetailsEntity
    suspend fun setMovieAsFavorite(movieId: Int, isFavorite: Boolean)
}