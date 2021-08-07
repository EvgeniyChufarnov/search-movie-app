package com.example.searchmovieapp.ui.details

import com.example.searchmovieapp.data.remote.entities.MovieDetailsEntity

interface MovieDetailsContract {
    interface View {
        fun showDetails(movieDetails: MovieDetailsEntity)
        fun showOnLostConnectionMessage()
        fun hideOnLostConnectionMessage()
        fun showConnectionError(message: String?)
        fun showProgressBar()
        fun hideProgressBar()
    }

    interface Presenter {
        fun attach(view: View, movieId: Int)
        fun detach()
        fun changeMovieFavoriteState(movieDetails: MovieDetailsEntity)
    }
}