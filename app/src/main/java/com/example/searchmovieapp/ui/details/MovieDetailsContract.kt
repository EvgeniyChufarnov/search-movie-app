package com.example.searchmovieapp.ui.details

import com.example.searchmovieapp.entities.MovieDetailsEntity

interface MovieDetailsContract {
    interface View {
        fun showDetails(movieDetails: MovieDetailsEntity)
        fun showOnLostConnectionMessage()
        fun hideOnLostConnectionMessage()
        fun showConnectionError(message: String?)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun getMovieDetails(movieId: Int)
        fun changeMovieFavoriteState(movieDetails: MovieDetailsEntity)
    }
}