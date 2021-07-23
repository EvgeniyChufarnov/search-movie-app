package com.example.searchmovieapp.ui.home

import android.os.Parcelable
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*

class HomePresenter(private val movieRepository: MovieRepository) :
    HomeContract.Presenter {

    private var view: HomeContract.View? = null
    private var requestNowPlayingPageNum = 1
    private var requestUpcomingPageNum = 1
    private var savedNowPlayingPosition: Parcelable? = null
    private var savedUpcomingPosition: Parcelable? = null
    private var isFirstLoading = true

    private lateinit var scope: CoroutineScope

    override fun attach(view: HomeContract.View) {
        this.view = view
        scope = CoroutineScope(Job() + Dispatchers.Main)
    }

    override fun firstLoadingDone() {
        isFirstLoading = false
    }

    override fun detach() {
        view = null
        scope.cancel()
    }

    override fun isFirstLoading() = isFirstLoading

    override fun getMovies() {
        getNowPlayingMovies()
        getUpcomingMovies()
    }

    private fun getNowPlayingMovies() {
        scope.launch {
            view?.showNowPlaying(requestNowPlayingMovies())

            savedNowPlayingPosition?.let {
                view?.restoreNowPlayingRecyclerViewPosition(it)
                savedNowPlayingPosition = null
            }
        }
    }

    private fun getUpcomingMovies() {
        scope.launch {
            view?.showUpcoming(requestUpcomingMovies())

            savedUpcomingPosition?.let {
                view?.restoreUpcomingRecyclerViewPosition(it)
                savedUpcomingPosition = null
            }
        }
    }

    private suspend fun requestNowPlayingMovies() = withContext(Dispatchers.IO) {
        movieRepository.getNowPlayingMovies(requestNowPlayingPageNum)
    }

    private suspend fun requestUpcomingMovies() = withContext(Dispatchers.IO) {
        movieRepository.getUpcomingMovies(requestUpcomingPageNum)
    }

    override fun changeMovieFavoriteState(movieId: Int) {
        movieRepository.changeMovieFavoriteState(movieId)
    }

    override fun loadMoreNowPlaying() {
        requestNowPlayingPageNum++
        getNowPlayingMovies()
    }

    override fun loadMoreUpcoming() {
        requestUpcomingPageNum++
        getUpcomingMovies()
    }

    override fun saveNowPlayingRecyclerViewPosition(position: Parcelable) {
        savedNowPlayingPosition = position
    }

    override fun saveUpcomingRecyclerRecyclerViewPosition(position: Parcelable) {
        savedUpcomingPosition = position
    }
}