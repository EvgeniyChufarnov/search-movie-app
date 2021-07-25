package com.example.searchmovieapp.ui.ratings

import android.os.Parcelable
import com.example.searchmovieapp.entities.MovieEntity

interface RatingsContract {
    interface View {
        fun showMovies(movies: List<MovieEntity>)
        fun restoreRecyclerViewPosition(position: Parcelable)
        fun showOnLostConnectionMessage()
        fun hideOnLostConnectionMessage()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun isFirstLoading(): Boolean
        fun firstLoadingDone()
        fun getTopRatedMovies()
        fun changeMovieFavoriteState(movieId: Int)
        fun loadMore()
        fun saveRecyclerViewPosition(position: Parcelable)
    }
}