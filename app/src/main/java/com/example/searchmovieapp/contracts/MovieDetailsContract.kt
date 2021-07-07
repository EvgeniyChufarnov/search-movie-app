package com.example.searchmovieapp.contracts

import com.example.searchmovieapp.entities.MovieDetailsEntity

interface MovieDetailsContract {
    interface View {
        fun showDetails(movieDetails: MovieDetailsEntity)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun getMovieDetails(movieId: Int)
    }
}