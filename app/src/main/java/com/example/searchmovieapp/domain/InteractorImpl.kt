package com.example.searchmovieapp.domain

import com.example.searchmovieapp.data.remote.entities.MovieDetailsEntity
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.data.ResultWrapper
import com.example.searchmovieapp.domain.repositories.FavoritesRepository
import com.example.searchmovieapp.domain.repositories.LocalMoviesRepository
import com.example.searchmovieapp.domain.repositories.MovieDetailsRepository
import com.example.searchmovieapp.domain.repositories.MoviesRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

private const val FIRST_PAGE = 1

class InteractorImpl @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val localMoviesRepository: LocalMoviesRepository,
    private val movieDetailsRepository: MovieDetailsRepository,
    private val favoritesRepository: FavoritesRepository,
    private val language: String
) : Interactor {
    private var favoritesCache: MutableMap<Int, MovieEntity>? = null
    private var isCacheLoaded = false
    private var mutex = Mutex()
    private var isForcedUpdateNeeded = false

    override suspend fun forcedUpdate() {
        if (isForcedUpdateNeeded) {
            val nowPlayingMovies = localMoviesRepository.getNowPlayingMovies(FIRST_PAGE, language)
            val upcomingMovies = localMoviesRepository.getUpcomingMovies(FIRST_PAGE, language)
            val topRatedMovies = localMoviesRepository.getTopRatedMovies(FIRST_PAGE, language)


            if (nowPlayingMovies is ResultWrapper.Success && upcomingMovies is ResultWrapper.Success
                && topRatedMovies is ResultWrapper.Success) {
                localMoviesRepository.clear()

                localMoviesRepository.cacheNowPlayingMovies(FIRST_PAGE, language, nowPlayingMovies.value)
                localMoviesRepository.cacheUpcomingMovies(FIRST_PAGE, language, upcomingMovies.value)
                localMoviesRepository.cacheTopRatedMovies(FIRST_PAGE, language, topRatedMovies.value)
            }
        }
    }

    override fun getNowPlayingTotalPages() = moviesRepository.nowPlayingTotalPages

    override fun getUpcomingTotalPages() = moviesRepository.upcomingTotalPages

    override fun getTopRatedTotalPages() = moviesRepository.topRatedTotalPages

    override suspend fun getNowPlayingMovies(
        page: Int
    ): ResultWrapper<List<MovieEntity>> {
        if (page > moviesRepository.nowPlayingTotalPages) return ResultWrapper.Success(emptyList())

        val localCache = localMoviesRepository.getNowPlayingMovies(page, language)

        return if (localCache is ResultWrapper.Success && localCache.value.isNotEmpty()) {
            isForcedUpdateNeeded = true
            localCache.checkFavoritesState()
        } else {
            val result = moviesRepository.getNowPlayingMovies(page, language)

            if (result is ResultWrapper.Success) {
                localMoviesRepository.cacheNowPlayingMovies(page, language, result.value)
            }

            result.checkFavoritesState()
        }
    }

    override suspend fun getUpcomingMovies(
        page: Int
    ): ResultWrapper<List<MovieEntity>> {
        if (page > moviesRepository.upcomingTotalPages) return ResultWrapper.Success(emptyList())

        val localCache = localMoviesRepository.getUpcomingMovies(page, language)

        return if (localCache is ResultWrapper.Success && localCache.value.isNotEmpty()) {
            isForcedUpdateNeeded = true
            localCache.checkFavoritesState()
        } else {
            val result = moviesRepository.getUpcomingMovies(page, language)

            if (result is ResultWrapper.Success) {
                localMoviesRepository.cacheUpcomingMovies(page, language, result.value)
            }

            result.checkFavoritesState()
        }
    }

    override suspend fun getTopRatedMovies(
        page: Int
    ): ResultWrapper<List<MovieEntity>> {
        if (page > moviesRepository.topRatedTotalPages) return ResultWrapper.Success(emptyList())

        val localCache = localMoviesRepository.getTopRatedMovies(page, language)

        return if (localCache is ResultWrapper.Success && localCache.value.isNotEmpty()) {
            localCache.checkFavoritesState()
        } else {
            val result = moviesRepository.getTopRatedMovies(page, language)

            if (result is ResultWrapper.Success) {
                localMoviesRepository.cacheTopRatedMovies(page, language, result.value)
            }

            result.checkFavoritesState()
        }
    }

    override suspend fun getAllLocalCachedNowPlayingMovies(): List<MovieEntity> {
        return localMoviesRepository.getAllLocalCachedNowPlayingMovies(language)
            .checkFavoritesState()
    }

    override suspend fun getAllLocalCachedUpcomingMovies(): List<MovieEntity> {
        return localMoviesRepository.getAllLocalCachedUpcomingMovies(language).checkFavoritesState()
    }

    override suspend fun getAllLocalCachedTopRatedMovies(): List<MovieEntity> {
        return localMoviesRepository.getAllLocalCachedTopRatedMovies(language).checkFavoritesState()
    }

    override suspend fun getMovieDetails(
        movieId: Int
    ): ResultWrapper<MovieDetailsEntity> {
        return movieDetailsRepository.getMovieDetails(movieId, language).checkFavoriteState()
    }

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

    private suspend fun ResultWrapper<List<MovieEntity>>.checkFavoritesState(): ResultWrapper<List<MovieEntity>> {
        if (this is ResultWrapper.Success) {
            value.checkFavoritesState()
        }

        return this
    }

    private suspend fun List<MovieEntity>.checkFavoritesState(): List<MovieEntity> {
        forEach { it.isFavorite = isMovieFavorite(it.id) }
        return this
    }

    private suspend fun ResultWrapper<MovieDetailsEntity>.checkFavoriteState(): ResultWrapper<MovieDetailsEntity> {
        if (this is ResultWrapper.Success) {
            value.isFavorite = isMovieFavorite(value.id)
        }

        return this
    }

    private suspend fun isMovieFavorite(movieId: Int): Boolean {
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