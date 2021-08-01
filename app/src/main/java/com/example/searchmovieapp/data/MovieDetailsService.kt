package com.example.searchmovieapp.data

import com.example.searchmovieapp.BuildConfig
import com.example.searchmovieapp.entities.MovieDetailsEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDetailsService {
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: String,
        @Query("language") language: String,
        @Query("api_key") apiKey: String = BuildConfig.MOVIES_API_KEY
    ): MovieDetailsEntity
}