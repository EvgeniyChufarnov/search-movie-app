package com.example.searchmovieapp.ui.ratings

import android.os.Parcelable
import com.example.searchmovieapp.domain.data.remote.entities.MovieEntity

interface RatingsContract {
    interface View {
        fun showMovies(movies: List<MovieEntity>)
        fun showMoreMovies(movies: List<MovieEntity>)
        fun restoreRecyclerViewPosition(position: Parcelable)
        fun showOnLostConnectionMessage()
        fun hideOnLostConnectionMessage()
        fun showConnectionError(message: String?)
        fun showProgressBar()
        fun hideProgressBar()
        fun navigateToMovieDetailFragment(movieId: Int)
        fun getRecyclerViewState(): Parcelable?
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun navigateToMovieDetailFragment(movieId: Int)
        fun changeMovieFavoriteState(movie: MovieEntity)
        fun loadMore()
    }
}