package com.example.searchmovieapp.repositories

import com.example.searchmovieapp.entities.MovieEntity

interface FavoritesRepository {
    suspend fun addToFavorites(movie: MovieEntity)
    suspend fun removeFromFavorites(movie: MovieEntity)
    suspend fun getFavorites(): List<MovieEntity>
}