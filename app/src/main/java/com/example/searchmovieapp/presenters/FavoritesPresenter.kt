package com.example.searchmovieapp.presenters

import com.example.searchmovieapp.contracts.FavoritesContract
import com.example.searchmovieapp.contracts.HomeContract
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*

class FavoritesPresenter(private val movieRepository: MovieRepository) :
    FavoritesContract.Presenter {

    private var view: FavoritesContract.View? = null
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun attach(view: FavoritesContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        scope.cancel()
    }

    override fun getMovies() {
        scope.launch {
            view?.showFavorites(getFavoriteMovies())
        }
    }

    private suspend fun getFavoriteMovies() = withContext(Dispatchers.IO) {
        movieRepository.getFavoritesMovies()
    }

    override fun changeMovieFavoriteState(movieId: Int) {
        movieRepository.changeMovieFavoriteState(movieId)
    }
}