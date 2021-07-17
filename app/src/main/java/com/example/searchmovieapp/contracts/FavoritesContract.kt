package com.example.searchmovieapp.contracts

import com.example.searchmovieapp.entities.MovieEntity

interface FavoritesContract {
    interface View {
        fun showFavorites(favoriteMovies: List<MovieEntity>)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun getMovies()
        fun changeMovieFavoriteState(movieId: Int)
    }
}