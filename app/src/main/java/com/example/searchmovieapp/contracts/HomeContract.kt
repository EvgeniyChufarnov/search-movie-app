package com.example.searchmovieapp.contracts

import com.example.searchmovieapp.entities.MovieEntity

interface HomeContract {
    interface View {
        fun showNowPlaying(nowPlayingMovies: List<MovieEntity>)
        fun showUpcoming(upcomingMovies: List<MovieEntity>)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun getMovies()
    }
}