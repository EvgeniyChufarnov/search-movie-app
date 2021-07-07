package com.example.searchmovieapp.injection

import com.example.searchmovieapp.data.FakeRemoteDataSourceImpl
import com.example.searchmovieapp.presenters.HomePresenterFactory
import com.example.searchmovieapp.presenters.MovieDetailsPresenterFactory
import com.example.searchmovieapp.repositories.MovieRepository

class AppContainer {
    private val remoteDataSource = FakeRemoteDataSourceImpl()
    private val movieRepository = MovieRepository(remoteDataSource)
    val homePresenterFactory = HomePresenterFactory(movieRepository)
    val movieDetailsPresenterFactory = MovieDetailsPresenterFactory(movieRepository)
}