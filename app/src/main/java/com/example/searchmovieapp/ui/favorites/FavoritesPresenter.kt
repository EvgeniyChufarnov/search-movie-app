package com.example.searchmovieapp.ui.favorites

import android.os.Parcelable
import com.example.searchmovieapp.ConnectionState
import com.example.searchmovieapp.ConnectionStateEvent
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.repositories.FavoritesRepository
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FavoritesPresenter(private val favoritesRepository: FavoritesRepository) :
    FavoritesContract.Presenter {

    private var view: FavoritesContract.View? = null
    private var savedPosition: Parcelable? = null
    private var isFirstLoading = true
    private var isLoadingCanceled = false

    private lateinit var scope: CoroutineScope

    override fun attach(view: FavoritesContract.View) {
        this.view = view
        scope = CoroutineScope(Job() + Dispatchers.Main)
        EventBus.getDefault().register(this)
    }

    override fun firstLoadingDone() {
        isFirstLoading = false
    }

    override fun detach() {
        view = null
        scope.cancel()
        isLoadingCanceled = false
        EventBus.getDefault().unregister(this)
    }

    override fun isFirstLoading() = isFirstLoading

    override fun getMovies() {
        if (ConnectionState.isAvailable) {
            scope.launch {
                view?.showFavorites(favoritesRepository.getFavoritesMovies())

                savedPosition?.let {
                    view?.restoreRecyclerViewPosition(it)
                    savedPosition = null
                }
            }
        } else {
            view?.showOnLostConnectionMessage()
            isLoadingCanceled = true
        }
    }

    override fun changeMovieFavoriteState(movie: MovieEntity) {
        scope.launch {
            if (movie.isFavorite) {
                favoritesRepository.removeFromFavorites(movie)
            } else {
                favoritesRepository.addToFavorites(movie)
            }

            getMovies()
        }
    }

    override fun saveRecyclerViewPosition(position: Parcelable) {
        savedPosition = position
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectionStateChangedEvent(event: ConnectionStateEvent) {

        if (ConnectionState.isAvailable) {
            view?.hideOnLostConnectionMessage()

            scope = CoroutineScope(Job() + Dispatchers.Main)

            if (isLoadingCanceled) {
                isLoadingCanceled = false
                getMovies()
            }
        } else {
            view?.showOnLostConnectionMessage()
            scope.cancel()
        }
    }
}