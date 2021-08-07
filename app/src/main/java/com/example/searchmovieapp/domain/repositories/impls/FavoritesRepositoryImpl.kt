package com.example.searchmovieapp.domain.repositories.impls

import com.example.searchmovieapp.data.local.FavoritesDao
import com.example.searchmovieapp.data.local.entities.CachedFavoriteMovieEntity
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.repositories.FavoritesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

private fun MovieEntity.toCachedFavoriteMovieEntity() =
    CachedFavoriteMovieEntity(id, title, posterPath, releaseDate, voteAverage, isUpcoming)

private fun CachedFavoriteMovieEntity.toMovieEntity() =
    MovieEntity(id, title, posterPath, releaseDate, voteAverage).apply { isFavorite = true }

class FavoritesRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoritesDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    FavoritesRepository {

    override suspend fun addToFavorites(movie: MovieEntity) = withContext(dispatcher) {
        favoritesDao.addToFavorites(movie.toCachedFavoriteMovieEntity())
    }

    override suspend fun removeFromFavorites(movie: MovieEntity) = withContext(dispatcher) {
        favoritesDao.removeFromFavorites(movie.toCachedFavoriteMovieEntity())
    }

    override suspend fun getFavoritesMovies(): List<MovieEntity> = withContext(dispatcher) {
        favoritesDao.getFavoritesMovies().map { it.toMovieEntity() }
    }
}