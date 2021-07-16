package com.example.searchmovieapp.presenters

import com.example.searchmovieapp.contracts.HomeContract
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*

class HomePresenter(private val movieRepository: MovieRepository) :
    HomeContract.Presenter {

    private var view: HomeContract.View? = null
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun attach(view: HomeContract.View) {
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