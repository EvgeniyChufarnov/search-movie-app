package com.example.searchmovieapp.repositories

import com.example.searchmovieapp.data.RemoteDataSource

class MovieRepository(private val remoteDataSource: RemoteDataSource) {
    suspend fun getNowPlayingMovies() = remoteDataSource.getNowPlayingMovies()
    suspend fun getUpcomingMovies() = remoteDataSource.getUpcomingMovies()
    suspend fun getFavoritesMovies() = remoteDataSource.getFavoritesMovies()
    suspend fun getTopRatedMovies() = remoteDataSource.getTopRatedMovies()
    suspend fun getMovieDetailsById(movieId: Int) =  remoteDataSource.getMovieDetailsById(movieId)
}