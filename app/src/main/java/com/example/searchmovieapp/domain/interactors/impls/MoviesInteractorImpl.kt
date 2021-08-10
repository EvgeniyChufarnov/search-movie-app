package com.example.searchmovieapp.domain.interactors.impls

import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.data.ResultWrapper
import com.example.searchmovieapp.domain.interactors.FavoritesInteractor
import com.example.searchmovieapp.domain.interactors.MoviesInteractor
import com.example.searchmovieapp.domain.repositories.LocalMoviesRepository
import com.example.searchmovieapp.domain.repositories.MoviesRepository
import javax.inject.Inject

private const val FIRST_PAGE = 1

class MoviesInteractorImpl @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val localMoviesRepository: LocalMoviesRepository,
    private val favoritesInteractor: FavoritesInteractor,
    private val language: String
) : MoviesInteractor {
    private var isForcedUpdateNeeded = false

    override suspend fun forcedUpdate() {
        if (isForcedUpdateNeeded) {
            val nowPlayingMovies = moviesRepository.getNowPlayingMovies(FIRST_PAGE, language)
            val upcomingMovies = moviesRepository.getUpcomingMovies(FIRST_PAGE, language)
            val topRatedMovies = moviesRepository.getTopRatedMovies(FIRST_PAGE, language)

            if (nowPlayingMovies is ResultWrapper.Success && upcomingMovies is ResultWrapper.Success
                && topRatedMovies is ResultWrapper.Success
            ) {
                localMoviesRepository.clear()

                localMoviesRepository.cacheNowPlayingMovies(
                    FIRST_PAGE,
                    language,
                    nowPlayingMovies.value
                )
                localMoviesRepository.cacheUpcomingMovies(
                    FIRST_PAGE,
                    language,
                    upcomingMovies.value
                )
                localMoviesRepository.cacheTopRatedMovies(
                    FIRST_PAGE,
                    language,
                    topRatedMovies.value
                )
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

    private suspend fun ResultWrapper<List<MovieEntity>>.checkFavoritesState(): ResultWrapper<List<MovieEntity>> {
        if (this is ResultWrapper.Success) {
            value.checkFavoritesState()
        }

        return this
    }

    private suspend fun List<MovieEntity>.checkFavoritesState(): List<MovieEntity> {
        forEach { it.isFavorite = favoritesInteractor.isMovieFavorite(it.id) }
        return this
    }
}