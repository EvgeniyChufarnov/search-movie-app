package com.example.searchmovieapp.domain.interactors.impls

import com.example.searchmovieapp.data.remote.entities.MovieDetailsEntity
import com.example.searchmovieapp.domain.data.ResultWrapper
import com.example.searchmovieapp.domain.interactors.FavoritesInteractor
import com.example.searchmovieapp.domain.interactors.MovieDetailsInteractor
import com.example.searchmovieapp.domain.repositories.MovieDetailsRepository
import javax.inject.Inject

class MovieDetailsInteractorImpl @Inject constructor(
    private val movieDetailsRepository: MovieDetailsRepository,
    private val favoritesInteractor: FavoritesInteractor,
    private val language: String
) : MovieDetailsInteractor {

    override suspend fun getMovieDetails(
        movieId: Int
    ): ResultWrapper<MovieDetailsEntity> {
        return movieDetailsRepository.getMovieDetails(movieId, language).checkFavoriteState()
    }

    private suspend fun ResultWrapper<MovieDetailsEntity>.checkFavoriteState(): ResultWrapper<MovieDetailsEntity> {
        if (this is ResultWrapper.Success) {
            value.isFavorite = favoritesInteractor.isMovieFavorite(value.id)
        }

        return this
    }
}