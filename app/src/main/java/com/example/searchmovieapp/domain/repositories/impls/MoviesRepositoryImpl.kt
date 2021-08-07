package com.example.searchmovieapp.domain.repositories.impls

import com.example.searchmovieapp.data.remote.MoviesService
import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.data.safeApiCall
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.repositories.LocalMoviesRepository
import com.example.searchmovieapp.domain.repositories.MoviesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

private const val POSTER_PATH = "https://image.tmdb.org/t/p/w500/"

private fun MovieEntity.initEntity(isUpcoming: Boolean) {
    this.posterPath = "$POSTER_PATH${this.posterPath}"
    this.isUpcoming = isUpcoming
}

class MoviesRepositoryImpl @Inject constructor(
    private val moviesService: MoviesService,
    private val localMoviesRepository: LocalMoviesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : MoviesRepository {
    override var nowPlayingTotalPages: Int = Int.MAX_VALUE
    override var upcomingTotalPages: Int = Int.MAX_VALUE
    override var topRatedTotalPages: Int = Int.MAX_VALUE

    override suspend fun getNowPlayingMovies(
        page: Int,
        language: String
    ): ResultWrapper<List<MovieEntity>> {
        if (page > nowPlayingTotalPages) return ResultWrapper.Success(emptyList())

        val localCache = localMoviesRepository.getNowPlayingMovies(page, language)

        if (localCache is ResultWrapper.Success && localCache.value.isNotEmpty()) {
            return localCache
        } else {
            val result = safeApiCall(dispatcher) {
                moviesService.getNowPlayingMovies(
                    page.toString(),
                    language
                ).also {
                    if (nowPlayingTotalPages == Int.MAX_VALUE) {
                        nowPlayingTotalPages = it.totalPages
                    }
                }.results.onEach { it.initEntity(false) }
            }

            if (result is ResultWrapper.Success) {
                localMoviesRepository.cacheNowPlayingMovies(page, language, result.value)
            }

            return result
        }
    }

    override suspend fun getUpcomingMovies(
        page: Int,
        language: String
    ): ResultWrapper<List<MovieEntity>> {
        if (page > upcomingTotalPages) return ResultWrapper.Success(emptyList())

        val localCache = localMoviesRepository.getUpcomingMovies(page, language)

        if (localCache is ResultWrapper.Success && localCache.value.isNotEmpty()) {
            return localCache
        } else {
            val result = safeApiCall(dispatcher) {
                moviesService.getUpcomingMovies(
                    page.toString(),
                    language
                ).also {
                    if (upcomingTotalPages == Int.MAX_VALUE) {
                        upcomingTotalPages = it.totalPages
                    }
                }.results.onEach { it.initEntity(true) }
            }

            if (result is ResultWrapper.Success) {
                localMoviesRepository.cacheUpcomingMovies(page, language, result.value)
            }

            return result
        }
    }

    override suspend fun getTopRatedMovies(
        page: Int,
        language: String
    ): ResultWrapper<List<MovieEntity>> {
        if (page > topRatedTotalPages) return ResultWrapper.Success(emptyList())

        val localCache = localMoviesRepository.getTopRatedMovies(page, language)

        if (localCache is ResultWrapper.Success && localCache.value.isNotEmpty()) {
            return localCache
        } else {
            val result = safeApiCall(dispatcher) {
                moviesService.getTopRatedMovies(
                    page.toString(),
                    language
                ).also {
                    if (topRatedTotalPages == Int.MAX_VALUE) {
                        topRatedTotalPages = it.totalPages
                    }
                }.results.onEach { it.initEntity(false) }
            }

            if (result is ResultWrapper.Success) {
                localMoviesRepository.cacheTopRatedMovies(page, language, result.value)
            }

            return result
        }
    }

    override suspend fun getAllLocalCachedNowPlayingMovies(language: String): List<MovieEntity> {
        return localMoviesRepository.getAllLocalCachedNowPlayingMovies(language)
    }

    override suspend fun getAllLocalCachedUpcomingMovies(language: String): List<MovieEntity> {
        return localMoviesRepository.getAllLocalCachedUpcomingMovies(language)
    }

    override suspend fun getAllLocalCachedTopRatedMovies(language: String): List<MovieEntity> {
        return localMoviesRepository.getAllLocalCachedTopRatedMovies(language)
    }
}
