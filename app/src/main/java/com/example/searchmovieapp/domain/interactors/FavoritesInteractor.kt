package com.example.searchmovieapp.domain.interactors

import com.example.searchmovieapp.data.remote.entities.MovieEntity

interface FavoritesInteractor {
    suspend fun addToFavorites(movie: MovieEntity)
    suspend fun removeFromFavorites(movie: MovieEntity)
    suspend fun getFavoritesMovies(): List<MovieEntity>
    suspend fun isMovieFavorite(movieId: Int): Boolean
}