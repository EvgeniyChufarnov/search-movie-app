package com.example.searchmovieapp.repositories.impls

import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.repositories.LocalMoviesRepository

class LocalMoviesRepositoryImpl : LocalMoviesRepository {
    private val nowPlayingCache = mutableMapOf<CacheInfo, List<MovieEntity>>()
    private val upcomingCache = mutableMapOf<CacheInfo, List<MovieEntity>>()
    private val topRatedCache = mutableMapOf<CacheInfo, List<MovieEntity>>()

    override suspend fun cacheNowPlayingMovies(
        page: Int,
        language: String,
        movies: List<MovieEntity>
    ) {
        nowPlayingCache[CacheInfo(page, language)] = movies
    }

    override suspend fun cacheUpcomingMovies(
        page: Int,
        language: String,
        movies: List<MovieEntity>
    ) {
        upcomingCache[CacheInfo(page, language)] = movies
    }

    override suspend fun cacheTopRatedMovies(
        page: Int,
        language: String,
        movies: List<MovieEntity>
    ) {
        topRatedCache[CacheInfo(page, language)] = movies
    }

    override suspend fun getNowPlayingMovies(
        page: Int,
        language: String
    ): ResultWrapper<List<MovieEntity>> {
        return wrapResult(nowPlayingCache[CacheInfo(page, language)])
    }

    override suspend fun getUpcomingMovies(
        page: Int,
        language: String
    ): ResultWrapper<List<MovieEntity>> {
        return wrapResult(upcomingCache[CacheInfo(page, language)])
    }

    override suspend fun getTopRatedMovies(
        page: Int,
        language: String
    ): ResultWrapper<List<MovieEntity>> {
        return wrapResult(topRatedCache[CacheInfo(page, language)])
    }

    override suspend fun getAllLocalCachedNowPlayingMovies(language: String): List<MovieEntity> {
        return nowPlayingCache.filter { it.key.language == language }.values.flatten()
    }

    override suspend fun getAllLocalCachedUpcomingMovies(language: String): List<MovieEntity> {
        return upcomingCache.filter { it.key.language == language }.values.flatten()
    }

    override suspend fun getAllLocalCachedTopRatedMovies(language: String): List<MovieEntity> {
        return topRatedCache.filter { it.key.language == language }.values.flatten()
    }

    private fun wrapResult(cache: List<MovieEntity>?) = if (cache == null) {
        ResultWrapper.GenericError(null, null)
    } else {
        ResultWrapper.Success(cache)
    }
}

data class CacheInfo(
    val page: Int,
    val language: String
)