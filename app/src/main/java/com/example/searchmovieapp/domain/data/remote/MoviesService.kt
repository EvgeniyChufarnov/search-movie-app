package com.example.searchmovieapp.data.remote

import com.example.searchmovieapp.BuildConfig
import com.example.searchmovieapp.data.remote.entities.MoviesResultEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: String,
        @Query("language") language: String,
        @Query("api_key") apiKey: String = BuildConfig.MOVIES_API_KEY
    ): MoviesResultEntity

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: String,
        @Query("language") language: String,
        @Query("api_key") apiKey: String = BuildConfig.MOVIES_API_KEY
    ): MoviesResultEntity

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: String,
        @Query("language") language: String,
        @Query("api_key") apiKey: String = BuildConfig.MOVIES_API_KEY
    ): MoviesResultEntity
}