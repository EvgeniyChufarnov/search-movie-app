package com.example.searchmovieapp.repositories.impls

import com.example.searchmovieapp.data.MovieDetailsService
import com.example.searchmovieapp.data.safeApiCall
import com.example.searchmovieapp.entities.MovieDetailsEntity
import com.example.searchmovieapp.repositories.MovieDetailsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

private const val POSTER_PATH = "https://image.tmdb.org/t/p/w500/"

private fun MovieDetailsEntity.initEntity(): MovieDetailsEntity {
    this.posterPath = "$POSTER_PATH${this.posterPath}"
    return this
}

class MovieDetailsRepositoryImpl @Inject constructor(
    private val movieDetailsService: MovieDetailsService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : MovieDetailsRepository {

    override suspend fun getMovieDetails(movieId: Int, language: String) = safeApiCall(dispatcher) {
        movieDetailsService.getMovieDetails(
            movieId.toString(),
            language
        ).initEntity()
    }
}