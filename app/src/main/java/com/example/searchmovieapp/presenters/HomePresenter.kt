package com.example.searchmovieapp.presenters

import com.example.searchmovieapp.contracts.HomeScreenContract
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*

class HomePresenter(private val movieRepository: MovieRepository) :
    HomeScreenContract.Presenter {

    private var view: HomeScreenContract.View? = null
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun attach(view: HomeScreenContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        scope.cancel()
    }

    override fun getMovies() {
        scope.launch {
            view?.showNowPlaying(getNowPlayingMovies())
        }

        scope.launch {
            view?.showUpcoming(getUpcomingMovies())
        }
    }

    private suspend fun getNowPlayingMovies() = withContext(Dispatchers.IO) {
        movieRepository.getNowPlayingMovies()
    }

    private suspend fun getUpcomingMovies() = withContext(Dispatchers.IO) {
        movieRepository.getUpcomingMovies()
    }
}