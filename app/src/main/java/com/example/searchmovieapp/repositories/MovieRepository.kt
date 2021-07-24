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
        //todo implement getting all favorites movies
        favoriteMoviesIds = remoteDataSource.getFavoritesMovies(1).map { it.id }.toMutableList()
    }

    private val nowPlayingCache: MutableMap<Int, List<MovieEntity>> = mutableMapOf()
    private val upcomingCache: MutableMap<Int, List<MovieEntity>> = mutableMapOf()
    private val favoritesCache: MutableMap<Int, List<MovieEntity>> = mutableMapOf()
    private val topRatedCache: MutableMap<Int, List<MovieEntity>> = mutableMapOf()

    suspend fun getNowPlayingMovies(pageNum: Int): List<MovieEntity> {
        waitForFavoritesToLoad()

        if (!nowPlayingCache.containsKey(pageNum)) {
            nowPlayingCache[pageNum] = remoteDataSource.getNowPlayingMovies(pageNum)
        }

        return nowPlayingCache.values.flatten()
    }

    suspend fun getUpcomingMovies(pageNum: Int): List<MovieEntity> {
        waitForFavoritesToLoad()

        if (!upcomingCache.containsKey(pageNum)) {
            upcomingCache[pageNum] = remoteDataSource.getUpcomingMovies(pageNum)
        }

        return upcomingCache.values.flatten()
    }

    suspend fun getFavoritesMovies(pageNum: Int): List<MovieEntity> {
        if (!favoritesCache.containsKey(pageNum)) {
            favoritesCache[pageNum] = remoteDataSource.getFavoritesMovies(pageNum)
        }

        return favoritesCache.values.flatten()
    }

    suspend fun getTopRatedMovies(pageNum: Int): List<MovieEntity> {
        if (!topRatedCache.containsKey(pageNum)) {
            topRatedCache[pageNum] = remoteDataSource.getTopRatedMovies(pageNum)
        }

        return topRatedCache.values.flatten()
    }

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