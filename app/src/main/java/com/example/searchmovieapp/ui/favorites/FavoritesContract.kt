package com.example.searchmovieapp.ui.favorites

import android.os.Parcelable
import com.example.searchmovieapp.data.remote.entities.MovieEntity

interface FavoritesContract {
    interface View {
        fun showFavorites(favoriteMovies: List<MovieEntity>)
        fun restoreRecyclerViewPosition(position: Parcelable)
        fun showOnLostConnectionMessage()
        fun hideOnLostConnectionMessage()
        fun showProgressBar()
        fun hideProgressBar()
        fun showNoFavoritesMessage()
        fun hideNoFavoritesMessage()
        fun navigateToMovieDetailFragment(movieId: Int)
        fun getRecyclerViewState(): Parcelable?
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun navigateToMovieDetailFragment(movieId: Int)
        fun changeMovieFavoriteState(movie: MovieEntity)
    }
}