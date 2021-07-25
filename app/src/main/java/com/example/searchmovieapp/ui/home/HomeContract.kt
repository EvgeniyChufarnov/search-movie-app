package com.example.searchmovieapp.ui.home

import android.os.Parcelable
import com.example.searchmovieapp.entities.MovieEntity

interface HomeContract {
    interface View {
        fun showNowPlaying(nowPlayingMovies: List<MovieEntity>)
        fun showUpcoming(upcomingMovies: List<MovieEntity>)
        fun restoreNowPlayingRecyclerViewPosition(position: Parcelable)
        fun restoreUpcomingRecyclerViewPosition(position: Parcelable)
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
        fun loadMoreNowPlaying()
        fun loadMoreUpcoming()
        fun saveNowPlayingRecyclerViewPosition(position: Parcelable)
        fun saveUpcomingRecyclerRecyclerViewPosition(position: Parcelable)
    }
}