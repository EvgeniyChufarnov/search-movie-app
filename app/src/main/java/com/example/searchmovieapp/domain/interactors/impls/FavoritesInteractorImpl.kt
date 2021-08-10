package com.example.searchmovieapp.domain.interactors.impls

import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.interactors.FavoritesInteractor
import com.example.searchmovieapp.domain.repositories.FavoritesRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class FavoritesInteractorImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : FavoritesInteractor {
    private var favoritesCache: MutableMap<Int, MovieEntity>? = null
    private var isCacheLoaded = false
    private var mutex = Mutex()

    override suspend fun addToFavorites(movie: MovieEntity) {
        movie.isFavorite = true
        favoritesCache?.set(movie.id, movie)
        favoritesRepository.addToFavorites(movie)
    }

    override suspend fun removeFromFavorites(movie: MovieEntity) {
        movie.isFavorite = false
        favoritesCache?.remove(movie.id)
        favoritesRepository.removeFromFavorites(movie)
    }

    override suspend fun getFavoritesMovies(): List<MovieEntity> {
        return favoritesRepository.getFavoritesMovies()
    }

    override suspend fun isMovieFavorite(movieId: Int): Boolean {
        if (!isCacheLoaded) {
            waitForInit()
        }

        return favoritesCache?.containsKey(movieId) ?: false
    }

    private suspend fun waitForInit() {
        mutex.withLock {
            if (favoritesCache == null) {
                favoritesCache = mutableMapOf()

                getFavoritesMovies().forEach {
                    favoritesCache?.set(it.id, it)
                }

                isCacheLoaded = true
            }
        }
    }
}