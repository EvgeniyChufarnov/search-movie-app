package com.example.searchmovieapp.repositories.impls

import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.repositories.FavoritesRepository

class FavoritesRepositoryImpl : FavoritesRepository {
    private val favoritesCache = mutableMapOf<Int, MovieEntity>()

    override suspend fun addToFavorites(movie: MovieEntity) {
        movie.isFavorite = true
        favoritesCache[movie.id] = movie
    }

    override suspend fun removeFromFavorites(movie: MovieEntity) {
        movie.isFavorite = false
        favoritesCache.remove(movie.id)
    }

    override suspend fun getFavoritesMovies(): List<MovieEntity> {
        return favoritesCache.values.toList()
    }

    override suspend fun isMovieFavorite(movieId: Int): Boolean {
        return favoritesCache.containsKey(movieId)
    }
}