package com.example.searchmovieapp.ui.ratings

import android.os.Parcelable
import com.example.searchmovieapp.domain.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.ConnectionState
import com.example.searchmovieapp.domain.ConnectionStateEvent
import com.example.searchmovieapp.domain.data.ResultWrapper
import com.example.searchmovieapp.domain.interactors.FavoritesInteractor
import com.example.searchmovieapp.domain.interactors.MoviesInteractor
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RatingsPresenter(
    private val moviesInteractor: MoviesInteractor,
    private val favoritesInteractor: FavoritesInteractor
) :
    RatingsContract.Presenter {

    private var view: RatingsContract.View? = null
    private var requestPageNum = 1
    private var savedPosition: Parcelable? = null
    private var isFirstLoading = true
    private var isLoadingMore = false
    private var isLoadingCanceled = false

    private lateinit var scope: CoroutineScope

    override fun attach(view: RatingsContract.View) {
        this.view = view
        scope = CoroutineScope(Job() + Dispatchers.Main)
        EventBus.getDefault().register(this)

        if (isFirstLoading) {
            view.showProgressBar()
            getTopRatedMovies()
        } else {
            getAllCachedTopRatedMovies()
        }

        checkConnectionState()
    }

    override fun detach() {
        view = null
        scope.cancel()
        isLoadingCanceled = false
        EventBus.getDefault().unregister(this)
    }

    private fun getTopRatedMovies() {
        scope.launch {
            when (val response = getMovies(requestPageNum)) {
                is ResultWrapper.NetworkError -> view?.showConnectionError(null)
                is ResultWrapper.GenericError -> view?.showConnectionError(response.error?.message)
                is ResultWrapper.Success -> requestShowingMovies(response.value)
            }
        }
    }

    private fun checkConnectionState() {
        if (!ConnectionState.isAvailable) {
            view?.showOnLostConnectionMessage()
            view?.hideProgressBar()
            isLoadingCanceled = true
        }
    }

    private fun requestShowingMovies(movies: List<MovieEntity>) {
        if (isFirstLoading) {
            view?.hideProgressBar()
            isFirstLoading = false
        }

        if (isLoadingMore) {
            isLoadingMore = false

            view?.showMoreMovies(movies)
        } else {
            view?.showMovies(movies)
        }
    }

    private fun getAllCachedTopRatedMovies() {
        scope.launch {
            view?.showMovies(moviesInteractor.getAllLocalCachedTopRatedMovies())

            savedPosition?.let {
                view?.restoreRecyclerViewPosition(it)
                savedPosition = null
            }
        }
    }

    private suspend fun getMovies(pageNum: Int): ResultWrapper<List<MovieEntity>> {
        return moviesInteractor.getTopRatedMovies(pageNum)
    }

    override fun changeMovieFavoriteState(movie: MovieEntity) {
        scope.launch {
            if (movie.isFavorite) {
                favoritesInteractor.removeFromFavorites(movie)
            } else {
                favoritesInteractor.addToFavorites(movie)
            }
        }
    }

    override fun loadMore() {
        if (!isLoadingMore) {
            isLoadingMore = true

            if (requestPageNum + 1 <= moviesInteractor.getTopRatedTotalPages()) {
                requestPageNum++
                getTopRatedMovies()
            }
        }
    }

    override fun navigateToMovieDetailFragment(movieId: Int) {
        savedPosition = view?.getRecyclerViewState()
        view?.navigateToMovieDetailFragment(movieId)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectionStateChangedEvent(event: ConnectionStateEvent) {
        if (ConnectionState.isAvailable) {
            view?.hideOnLostConnectionMessage()

            if (isFirstLoading) {
                view?.showProgressBar()
            }

            scope = CoroutineScope(Job() + Dispatchers.Main)

            if (isLoadingCanceled) {
                isLoadingCanceled = false
                getTopRatedMovies()
            }
        } else {
            view?.showOnLostConnectionMessage()
            view?.hideProgressBar()
            scope.cancel()
        }
    }
}