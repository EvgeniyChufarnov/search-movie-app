package com.example.searchmovieapp.domain.interactors

import com.example.searchmovieapp.data.remote.entities.MovieDetailsEntity
import com.example.searchmovieapp.domain.data.ResultWrapper

interface MovieDetailsInteractor {
    suspend fun getMovieDetails(movieId: Int): ResultWrapper<MovieDetailsEntity>
}