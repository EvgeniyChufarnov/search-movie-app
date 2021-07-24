package com.example.searchmovieapp.ui.favorites

import android.os.Parcelable
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*

class FavoritesPresenter(private val movieRepository: MovieRepository) :
    FavoritesContract.Presenter {

    private var view: FavoritesContract.View? = null
    private var requestPageNum = 1
    private var savedPosition: Parcelable? = null
    private var isFirstLoading = true

    private lateinit var scope: CoroutineScope

    override fun attach(view: FavoritesContract.View) {
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
        scope.launch {
            view?.showFavorites(getFavoriteMovies())

            savedPosition?.let {
                view?.restoreRecyclerViewPosition(it)
                savedPosition = null
            }
        }
    }

    private suspend fun getFavoriteMovies() = withContext(Dispatchers.IO) {
        movieRepository.getFavoritesMovies(requestPageNum)
    }

    override fun changeMovieFavoriteState(movieId: Int) {
        movieRepository.changeMovieFavoriteState(movieId)
    }

    override fun loadMore() {
        requestPageNum++
        getMovies()
    }

    override fun saveRecyclerViewPosition(position: Parcelable) {
        savedPosition = position
    }
}