package com.example.searchmovieapp.repositories

import com.example.searchmovieapp.entities.MovieEntity

interface FavoritesRepository {
    suspend fun addToFavorites(movie: MovieEntity)
    suspend fun removeFromFavorites(movie: MovieEntity)
    suspend fun getFavoritesMovies(): List<MovieEntity>
    suspend fun isMovieFavorite(movieId: Int): Boolean
}