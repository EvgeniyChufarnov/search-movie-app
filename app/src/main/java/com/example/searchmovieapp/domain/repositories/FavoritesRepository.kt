package com.example.searchmovieapp.domain.repositories

import com.example.searchmovieapp.data.remote.entities.MovieEntity

interface FavoritesRepository {
    suspend fun addToFavorites(movie: MovieEntity)
    suspend fun removeFromFavorites(movie: MovieEntity)
    suspend fun getFavoritesMovies(): List<MovieEntity>
}