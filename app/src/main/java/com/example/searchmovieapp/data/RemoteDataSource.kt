package com.example.searchmovieapp.data

import com.example.searchmovieapp.entities.MovieDetailsEntity
import com.example.searchmovieapp.entities.MovieEntity

interface RemoteDataSource {
    suspend fun getNowPlayingMovies(pageNum: Int): List<MovieEntity>
    suspend fun getUpcomingMovies(pageNum: Int): List<MovieEntity>
    suspend fun getFavoritesMovies(pageNum: Int): List<MovieEntity>
    suspend fun getTopRatedMovies(pageNum: Int): List<MovieEntity>
    suspend fun getMovieDetailsById(movieId: Int): MovieDetailsEntity
    suspend fun setMovieAsFavorite(movieId: Int, isFavorite: Boolean)
}