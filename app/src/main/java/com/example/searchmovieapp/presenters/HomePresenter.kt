package com.example.searchmovieapp.presenters

import com.example.searchmovieapp.repositories.MovieRepository

class HomePresenter(private val movieRepository: MovieRepository) {
    fun getNowPlayingMovies() = movieRepository.getNowPlayingMovies()
    fun getUpcomingMovies() = movieRepository.getUpcomingMovies()
}