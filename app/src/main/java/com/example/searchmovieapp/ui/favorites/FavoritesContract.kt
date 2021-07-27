package com.example.searchmovieapp.ui.favorites

import android.os.Parcelable
import com.example.searchmovieapp.entities.MovieEntity

interface FavoritesContract {
    interface View {
        fun showFavorites(favoriteMovies: List<MovieEntity>)
        fun restoreRecyclerViewPosition(position: Parcelable)
        fun showOnLostConnectionMessage()
        fun hideOnLostConnectionMessage()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun isFirstLoading(): Boolean
        fun firstLoadingDone()
        fun getMovies()
        fun changeMovieFavoriteState(movieId: Int)
        fun loadMore()
        fun saveRecyclerViewPosition(position: Parcelable)
    }
}