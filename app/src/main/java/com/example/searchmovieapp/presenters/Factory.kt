package com.example.searchmovieapp.presenters

import com.example.searchmovieapp.contracts.HomeScreenContract
import com.example.searchmovieapp.repositories.MovieRepository

interface Factory<T> {
    fun create(): T
}

class HomePresenterFactory(private val movieRepository: MovieRepository) :
    Factory<HomeScreenContract.Presenter> {
    override fun create() = HomePresenter(movieRepository)
}