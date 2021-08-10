package com.example.searchmovieapp.ui.home

import android.os.Parcelable
import com.example.searchmovieapp.data.remote.entities.MovieEntity

interface HomeContract {
    interface View {
        fun showMovies(nowPlayingMovies: List<MovieEntity>, upcomingMovies: List<MovieEntity>)
        fun showMoreNowPlaying(nowPlayingMovies: List<MovieEntity>)
        fun showMoreUpcoming(upcomingMovies: List<MovieEntity>)
        fun restoreNowPlayingRecyclerViewPosition(position: Parcelable)
        fun restoreUpcomingRecyclerViewPosition(position: Parcelable)
        fun showOnLostConnectionMessage()
        fun hideOnLostConnectionMessage()
        fun showConnectionError(message: String?)
        fun showProgressBar()
        fun hideProgressBar()
        fun navigateToMovieDetailFragment(movieId: Int)
        fun getNowPlayingRecyclerViewState(): Parcelable?
        fun getUpcomingRecyclerViewState(): Parcelable?
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun navigateToMovieDetailFragment(movieId: Int)
        fun changeMovieFavoriteState(movie: MovieEntity)
        fun loadMoreNowPlaying()
        fun loadMoreUpcoming()
    }
}