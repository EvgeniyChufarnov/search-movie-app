package com.example.searchmovieapp.ui.ratings

import android.os.Parcelable
import com.example.searchmovieapp.ConnectionState
import com.example.searchmovieapp.ConnectionStateEvent
import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.repositories.FavoritesRepository
import com.example.searchmovieapp.domain.repositories.MoviesRepository
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
                is ResultWrapper.Success -> requestShowingMovies(response.value.checkFavoritesState())
            }
        }

        if (!ConnectionState.isAvailable) {
            view?.showOnLostConnectionMessage()
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

    private suspend fun List<MovieEntity>.checkFavoritesState(): List<MovieEntity> {
        forEach { it.isFavorite = favoritesRepository.isMovieFavorite(it.id) }
        return this
    }

    private fun getAllCachedTopRatedMovies() {
        scope.launch {
            view?.showMovies(
                moviesRepository.getAllLocalCachedTopRatedMovies(Locale.getDefault().language)
                    .checkFavoritesState()
            )

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
        if (!isLoadingMore) {
            isLoadingMore = true

            if (requestPageNum + 1 <= moviesRepository.topRatedTotalPages) {
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