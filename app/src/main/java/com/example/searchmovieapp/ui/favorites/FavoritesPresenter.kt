package com.example.searchmovieapp.ui.favorites

import android.os.Parcelable
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.ConnectionState
import com.example.searchmovieapp.domain.ConnectionStateEvent
import com.example.searchmovieapp.domain.interactors.FavoritesInteractor
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FavoritesPresenter(private val favoritesInteractor: FavoritesInteractor) :
    FavoritesContract.Presenter {

    private var view: FavoritesContract.View? = null
    private var savedPosition: Parcelable? = null
    private var isFirstLoading = true

    private lateinit var scope: CoroutineScope

    override fun attach(view: FavoritesContract.View) {
        this.view = view
        scope = CoroutineScope(Job() + Dispatchers.Main)
        EventBus.getDefault().register(this)

        if (isFirstLoading) {
            view.showProgressBar()
        }

        getMovies()
    }

    override fun detach() {
        view = null
        scope.cancel()
        isFirstLoading = false
        EventBus.getDefault().unregister(this)
    }

    private fun getMovies() {
        scope.launch {
            requestShowingMovies(favoritesInteractor.getFavoritesMovies())
        }

        if (!ConnectionState.isAvailable) {
            view?.showOnLostConnectionMessage()
        }
    }

    private fun requestShowingMovies(movies: List<MovieEntity>) {
        if (isFirstLoading) {
            view?.hideProgressBar()
            isFirstLoading = false
        }

        if (movies.isEmpty()) {
            view?.showNoFavoritesMessage()
        } else {
            view?.hideNoFavoritesMessage()
            view?.showFavorites(movies)

            savedPosition?.let {
                view?.restoreRecyclerViewPosition(it)
                savedPosition = null
            }
        }
    }

    override fun changeMovieFavoriteState(movie: MovieEntity) {
        scope.launch {
            if (movie.isFavorite) {
                favoritesInteractor.removeFromFavorites(movie)
            } else {
                favoritesInteractor.addToFavorites(movie)
            }

            getMovies()
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
        } else {
            view?.showOnLostConnectionMessage()
            view?.hideProgressBar()
        }
    }
}