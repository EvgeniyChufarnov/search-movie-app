package com.example.searchmovieapp.ui.ratings

import android.os.Parcelable
import com.example.searchmovieapp.ConnectionStateEvent
import com.example.searchmovieapp.ConnectionState
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Singleton


@Singleton
class RatingsPresenter(private val movieRepository: MovieRepository) :
    RatingsContract.Presenter {

    private var view: RatingsContract.View? = null
    private var requestPageNum = 1
    private var savedPosition: Parcelable? = null
    private var isFirstLoading = true
    private var isLoadingCanceled = false
    private var isCacheLoaded = false

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
        isCacheLoaded = false
        EventBus.getDefault().unregister(this)
    }

    override fun isFirstLoading() = isFirstLoading

    override fun getTopRatedMovies() {
        if (ConnectionState.isAvailable) {
            scope.launch {
                view?.showMovies(getMovies(requestPageNum))

                savedPosition?.let {
                    view?.restoreRecyclerViewPosition(it)
                    savedPosition = null
                }
            }
        } else {
            view?.showOnLostConnectionMessage()
            isLoadingCanceled = true

            if (!isCacheLoaded) {
                isCacheLoaded = true
                loadCache()
            }
        }
    }

    private suspend fun getMovies(pageNum: Int) = withContext(Dispatchers.IO) {
        movieRepository.getTopRatedMovies(pageNum)
    }

    override fun changeMovieFavoriteState(movieId: Int) {
        movieRepository.changeMovieFavoriteState(movieId)
    }

    private fun loadCache() {
        if (!isFirstLoading) {
            scope.launch {
                view?.showMovies(getMovies(if (requestPageNum == 1) 1 else requestPageNum - 1))

                savedPosition?.let {
                    view?.restoreRecyclerViewPosition(it)
                    savedPosition = null
                }
            }
        }
    }

    override fun loadMore() {
        requestPageNum++
        getTopRatedMovies()
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