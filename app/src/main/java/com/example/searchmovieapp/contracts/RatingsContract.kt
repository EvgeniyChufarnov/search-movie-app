package com.example.searchmovieapp.contracts

import com.example.searchmovieapp.entities.MovieEntity

interface RatingsContract {
    interface View {
        fun showMovies(movies: List<MovieEntity>)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun getTopRatedMovies()
    }
}