package com.example.searchmovieapp.ui.details

import com.example.searchmovieapp.entities.MovieDetailsEntity

interface MovieDetailsContract {
    interface View {
        fun showDetails(movieDetails: MovieDetailsEntity)
        fun showOnLostConnectionMessage()
        fun hideOnLostConnectionMessage()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun getMovieDetails(movieId: Int)
        fun changeMovieFavoriteState(movieId: Int)
    }
}