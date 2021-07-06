package com.example.searchmovieapp.presenters

import com.example.searchmovieapp.entities.MovieEntity

interface HomeScreenContract {
    interface HomeView {
        fun showNowPlaying(nowPlayingMovies: List<MovieEntity>)
        fun showUpcoming(upcomingMovies: List<MovieEntity>)
    }

    interface HomePresenter {
        fun attach(homeView: HomeView)
        fun detach()
        fun getMovies()
    }
}