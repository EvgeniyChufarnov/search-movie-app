package com.example.searchmovieapp.ui.ratings

import android.os.Parcelable
import com.example.searchmovieapp.ConnectionState
import com.example.searchmovieapp.ConnectionStateEvent
import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.repositories.FavoritesRepository
import com.example.searchmovieapp.repositories.MoviesRepository
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Singleton


@Singleton
class RatingsPresenter(
    private val moviesRepository: MoviesRepository,
    private val favoritesRepository: FavoritesRepository
) :
    RatingsContract.Presenter {

    private var view: RatingsContract.View? = null
    private var requestPageNum = 1
    private var savedPosition: Parcelable? = null
    private var isFirstLoading = true
    private var isLoadingCanceled = false

    private lateinit var scope: CoroutineScope

    override fun attach(view: RatingsContract.View) {
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

    override fun getTopRatedMovies() {
        scope.launch {
            when (val response = getMovies(requestPageNum)) {
                is ResultWrapper.NetworkError -> view?.showConnectionError(null)
                is ResultWrapper.GenericError -> view?.showConnectionError(response.error?.message)
                is ResultWrapper.Success -> view?.showMovies(response.value.checkFavoritesState())
            }
        }

        if (!ConnectionState.isAvailable) {
            view?.showOnLostConnectionMessage()
            isLoadingCanceled = true
        }
    }

    private suspend fun List<MovieEntity>.checkFavoritesState(): List<MovieEntity> {
        forEach {
            it.isFavorite = favoritesRepository.isMovieFavorite(it.id)
        }
        return this
    }

    override fun getAllCachedTopRatedMovies() {
        scope.launch {
            view?.showMovies(moviesRepository.getAllLocalCachedTopRatedMovies(Locale.getDefault().language))

            savedPosition?.let {
                view?.restoreRecyclerViewPosition(it)
                savedPosition = null
            }
        }
    }

    private suspend fun getMovies(pageNum: Int): ResultWrapper<List<MovieEntity>> {
        return moviesRepository.getTopRatedMovies(pageNum, Locale.getDefault().language)
    }

    override fun changeMovieFavoriteState(movie: MovieEntity) {
        scope.launch {
            if (movie.isFavorite) {
                favoritesRepository.removeFromFavorites(movie)
            } else {
                favoritesRepository.addToFavorites(movie)
            }
        }
    }

    override fun loadMore() {
        if (requestPageNum + 1 <= moviesRepository.topRatedTotalPages) {
            requestPageNum++
            getTopRatedMovies()
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
                getTopRatedMovies()
            }
        } else {
            view?.showOnLostConnectionMessage()
            scope.cancel()
        }
    }
}