package com.example.searchmovieapp.repositories.impls

import com.example.searchmovieapp.data.local.FavoritesDao
import com.example.searchmovieapp.data.local.entities.CachedFavoriteMovieEntity
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.repositories.FavoritesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

private fun MovieEntity.toCachedFavoriteMovieEntity() =
    CachedFavoriteMovieEntity(id, title, posterPath, releaseDate, voteAverage, isUpcoming)

private fun CachedFavoriteMovieEntity.toMovieEntity() =
    MovieEntity(id, title, posterPath, releaseDate, voteAverage).apply { isFavorite = true }

class FavoritesRepositoryImpl @Inject constructor(private val favoritesDao: FavoritesDao) :
    FavoritesRepository {
    private var favoritesCache: MutableMap<Int, MovieEntity>? = null
    private var isCacheLoaded = false
    private var mutex = Mutex()

    override suspend fun addToFavorites(movie: MovieEntity) = withContext(Dispatchers.IO) {
        movie.isFavorite = true
        favoritesCache?.set(movie.id, movie)
        favoritesDao.addToFavorites(movie.toCachedFavoriteMovieEntity())
    }

    override suspend fun removeFromFavorites(movie: MovieEntity) = withContext(Dispatchers.IO) {
        movie.isFavorite = false
        favoritesCache?.remove(movie.id)
        favoritesDao.removeFromFavorites(movie.toCachedFavoriteMovieEntity())
    }

    override suspend fun getFavoritesMovies(): List<MovieEntity> = withContext(Dispatchers.IO) {
        favoritesDao.getFavoritesMovies().map { it.toMovieEntity() }
    }

    override suspend fun isMovieFavorite(movieId: Int) = withContext(Dispatchers.IO) {
        if (!isCacheLoaded) {
            waitForInit()
        }

        favoritesCache?.containsKey(movieId) ?: false
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