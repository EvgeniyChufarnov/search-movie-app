package com.example.searchmovieapp.repositories

import com.example.searchmovieapp.data.RemoteDataSource

class MovieRepository(private val remoteDataSource: RemoteDataSource) {
    fun getNowPlayingMovies() = remoteDataSource.getNowPlayingMovies()
    fun getUpcomingMovies() = remoteDataSource.getUpcomingMovies()
}