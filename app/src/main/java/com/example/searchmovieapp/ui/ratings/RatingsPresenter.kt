package com.example.searchmovieapp.ui.ratings

import android.os.Parcelable
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*
import javax.inject.Singleton

@Singleton
class RatingsPresenter(private val movieRepository: MovieRepository) :
    RatingsContract.Presenter {

    private var view: RatingsContract.View? = null
    private var requestPageNum = 1
    private var savedPosition: Parcelable? = null
    private var isFirstLoading = true

    private lateinit var scope: CoroutineScope

    override fun attach(view: RatingsContract.View) {
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

    override fun getTopRatedMovies() {
        scope.launch {
            view?.showMovies(getMovies())

            savedPosition?.let {
                view?.restoreRecyclerViewPosition(it)
                savedPosition = null
            }
        }
    }

    private suspend fun getMovies() = withContext(Dispatchers.IO) {
        movieRepository.getTopRatedMovies(requestPageNum)
    }

    override fun changeMovieFavoriteState(movieId: Int) {
        movieRepository.changeMovieFavoriteState(movieId)
    }

    override fun loadMore() {
        requestPageNum++
        getTopRatedMovies()
    }

    override fun saveRecyclerViewPosition(position: Parcelable) {
        savedPosition = position
    }
}