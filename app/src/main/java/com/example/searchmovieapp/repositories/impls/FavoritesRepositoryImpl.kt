package com.example.searchmovieapp.repositories.impls

import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.repositories.FavoritesRepository

class FavoritesRepositoryImpl: FavoritesRepository {
    private val favoritesCache = mutableListOf<MovieEntity>()

    override suspend fun addToFavorites(movie: MovieEntity) {
        favoritesCache.add(movie)
    }

    override suspend fun removeFromFavorites(movie: MovieEntity) {
        favoritesCache.remove(movie)
    }

    override suspend fun getFavorites(): List<MovieEntity> {
        return favoritesCache
    }
}