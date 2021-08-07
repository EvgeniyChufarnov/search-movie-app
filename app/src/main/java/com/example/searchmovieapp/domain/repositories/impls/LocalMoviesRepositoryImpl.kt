package com.example.searchmovieapp.domain.repositories.impls

import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.data.local.MoviesDao
import com.example.searchmovieapp.data.local.entities.CachedMovieEntity
import com.example.searchmovieapp.data.local.entities.MovieEntityType
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.repositories.LocalMoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private fun MovieEntity.toCachedMovieEntity(
    page: Int,
    language: String,
    type: MovieEntityType
) = CachedMovieEntity(
    id,
    title,
    posterPath,
    releaseDate,
    voteAverage,
    isUpcoming,
    page,
    language,
    type.ordinal
)

private fun CachedMovieEntity.toMovieEntity(isUpcoming: Boolean) =
    MovieEntity(id, title, posterPath, releaseDate, voteAverage).apply { this.isUpcoming = isUpcoming }

class LocalMoviesRepositoryImpl @Inject constructor(private val moviesDao: MoviesDao) :
    LocalMoviesRepository {

    override suspend fun cacheNowPlayingMovies(
        page: Int,
        language: String,
        movies: List<MovieEntity>
    ) {
        withContext(Dispatchers.IO) {
            moviesDao.addMovies(*movies.map {
                it.toCachedMovieEntity(
                    page,
                    language,
                    MovieEntityType.NOW_PLAYING
                )
            }.toTypedArray())
        }
    }

    override suspend fun cacheUpcomingMovies(
        page: Int,
        language: String,
        movies: List<MovieEntity>
    ) {
        withContext(Dispatchers.IO) {
            moviesDao.addMovies(*movies.map {
                it.toCachedMovieEntity(
                    page,
                    language,
                    MovieEntityType.UPCOMING
                )
            }.toTypedArray())
        }
    }

    override suspend fun cacheTopRatedMovies(
        page: Int,
        language: String,
        movies: List<MovieEntity>
    ) {
        withContext(Dispatchers.IO) {
            moviesDao.addMovies(*movies.map {
                it.toCachedMovieEntity(
                    page,
                    language,
                    MovieEntityType.TOP_RATED
                )
            }.toTypedArray())
        }
    }

    override suspend fun getNowPlayingMovies(
        page: Int,
        language: String
    ): ResultWrapper<List<MovieEntity>> {
        return withContext(Dispatchers.IO) {
            wrapResult(
                moviesDao.getMoviesByPage(page, language, MovieEntityType.NOW_PLAYING.ordinal)
                    .map { it.toMovieEntity(false) })
        }
    }

    override suspend fun getUpcomingMovies(
        page: Int,
        language: String
    ): ResultWrapper<List<MovieEntity>> {
        return withContext(Dispatchers.IO) {
            wrapResult(
                moviesDao.getMoviesByPage(page, language, MovieEntityType.UPCOMING.ordinal)
                    .map { it.toMovieEntity(true) })
        }
    }

    override suspend fun getTopRatedMovies(
        page: Int,
        language: String
    ): ResultWrapper<List<MovieEntity>> {
        return withContext(Dispatchers.IO) {
            wrapResult(
                moviesDao.getMoviesByPage(page, language, MovieEntityType.TOP_RATED.ordinal)
                    .map { it.toMovieEntity(false) })
        }
    }

    override suspend fun getAllLocalCachedNowPlayingMovies(language: String): List<MovieEntity> {
        return withContext(Dispatchers.IO) {
            moviesDao.getMovies(language, MovieEntityType.NOW_PLAYING.ordinal)
                .map { it.toMovieEntity(false) }
        }
    }

    override suspend fun getAllLocalCachedUpcomingMovies(language: String): List<MovieEntity> {
        return withContext(Dispatchers.IO) {
            moviesDao.getMovies(language, MovieEntityType.UPCOMING.ordinal)
                .map { it.toMovieEntity(true) }
        }
    }

    override suspend fun getAllLocalCachedTopRatedMovies(language: String): List<MovieEntity> {
        return withContext(Dispatchers.IO) {
            moviesDao.getMovies(language, MovieEntityType.TOP_RATED.ordinal)
                .map { it.toMovieEntity(false) }
        }
    }

    private fun wrapResult(cache: List<MovieEntity>?) = if (cache == null) {
        ResultWrapper.GenericError(null, null)
    } else {
        ResultWrapper.Success(cache)
    }
}