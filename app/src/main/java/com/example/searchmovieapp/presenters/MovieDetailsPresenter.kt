package com.example.searchmovieapp.presenters

import com.example.searchmovieapp.contracts.MovieDetailsContract
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*

class MovieDetailsPresenter(private val movieRepository: MovieRepository) :
    MovieDetailsContract.Presenter {

    private var view: MovieDetailsContract.View? = null
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun attach(view: MovieDetailsContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        scope.cancel()
    }

    override fun getMovieDetails(movieId: Int) {
        scope.launch {
            view?.showDetails(getMovieDetailsFromRepository(movieId))
        }
    }

    private suspend fun getMovieDetailsFromRepository(movieId: Int) = withContext(Dispatchers.IO) {
        movieRepository.getMovieDetailsById(movieId)
    }

    override fun changeMovieFavoriteState(movieId: Int) {
        movieRepository.changeMovieFavoriteState(movieId)
    }
}