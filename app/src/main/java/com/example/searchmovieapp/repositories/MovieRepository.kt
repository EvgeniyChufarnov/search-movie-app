package com.example.searchmovieapp.repositories

import com.example.searchmovieapp.data.RemoteDataSource
import com.example.searchmovieapp.entities.MovieDetailsEntity
import com.example.searchmovieapp.entities.MovieEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Singleton

private var favoriteMoviesIds: MutableList<Int> = mutableListOf()

@Singleton
class MovieRepository(private val remoteDataSource: RemoteDataSource) {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val job: Job = scope.launch {
        favoriteMoviesIds = remoteDataSource.getFavoritesMovies().map { it.id }.toMutableList()
    }

    suspend fun getNowPlayingMovies(): List<MovieEntity> {
        waitForFavoritesToLoad()
        return remoteDataSource.getNowPlayingMovies()
    }

    suspend fun getUpcomingMovies(): List<MovieEntity> {
        waitForFavoritesToLoad()
        return remoteDataSource.getUpcomingMovies()
    }

    suspend fun getFavoritesMovies() = remoteDataSource.getFavoritesMovies()

    suspend fun getTopRatedMovies() = remoteDataSource.getTopRatedMovies()

    suspend fun getMovieDetailsById(movieId: Int) = remoteDataSource.getMovieDetailsById(movieId)

    private suspend fun waitForFavoritesToLoad() {
        if (!job.isCompleted) {
            job.join()
        }
    }

    fun changeMovieFavoriteState(movieId: Int) {
        val isFavorite = !favoriteMoviesIds.contains(movieId)
        editFavoriteMoviesIds(movieId, isFavorite)

        scope.launch {
            remoteDataSource.setMovieAsFavorite(movieId, isFavorite)
        }
    }

    private fun editFavoriteMoviesIds(movieId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteMoviesIds.add(movieId)
        } else {
            favoriteMoviesIds.remove(movieId)
        }
    }
}

fun MovieEntity.isFavorite() = favoriteMoviesIds.contains(id)

fun MovieDetailsEntity.isFavorite() = favoriteMoviesIds.contains(id)