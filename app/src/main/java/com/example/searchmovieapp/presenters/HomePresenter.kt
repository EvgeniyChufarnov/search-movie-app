package com.example.searchmovieapp.presenters

import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*

class HomePresenter(private val movieRepository: MovieRepository) :
    HomeScreenContract.HomePresenter {

    private var homeView: HomeScreenContract.HomeView? = null
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun attach(homeView: HomeScreenContract.HomeView) {
        this.homeView = homeView
    }

    override fun detach() {
        homeView = null
        scope.cancel()
    }

    override fun getMovies() {
        scope.launch {
            homeView?.showNowPlaying(getNowPlayingMovies())
        }

        scope.launch {
            homeView?.showUpcoming(getUpcomingMovies())
        }
    }

    private suspend fun getNowPlayingMovies() = withContext(Dispatchers.IO) {
        movieRepository.getNowPlayingMovies()
    }

    private suspend fun getUpcomingMovies() = withContext(Dispatchers.IO) {
        movieRepository.getUpcomingMovies()
    }
}