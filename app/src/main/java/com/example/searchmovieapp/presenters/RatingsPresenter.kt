package com.example.searchmovieapp.presenters

import com.example.searchmovieapp.contracts.RatingsContract
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*

class RatingsPresenter(private val movieRepository: MovieRepository) :
    RatingsContract.Presenter {

    private var view: RatingsContract.View? = null
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun attach(view: RatingsContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        scope.cancel()
    }

    override fun getTopRatedMovies() {
        scope.launch {
            view?.showMovies(getMovies())
        }
    }

    private suspend fun getMovies() = withContext(Dispatchers.IO) {
        movieRepository.getTopRatedMovies()
    }

    override fun changeMovieFavoriteState(movieId: Int) {
        movieRepository.changeMovieFavoriteState(movieId)
    }
}