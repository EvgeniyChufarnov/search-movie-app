package com.example.searchmovieapp.domain.interactors

import com.example.searchmovieapp.domain.data.remote.entities.MovieEntity

interface WorkInteractor {
    fun isWorkRegistered(movieId: Int): Boolean
    fun registerWork(movie: MovieEntity): Boolean
    fun unregisterWork(movie: MovieEntity)
}