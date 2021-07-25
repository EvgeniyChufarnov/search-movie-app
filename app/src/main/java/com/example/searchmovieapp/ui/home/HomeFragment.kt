package com.example.searchmovieapp.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.searchmovieapp.databinding.FragmentHomeBinding
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.ui.common.MovieListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val NUM_OF_ITEMS_FROM_LAST_ONE = 5

@AndroidEntryPoint
class HomeFragment : Fragment(),
    HomeContract.View {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isLoadingMoreNowPlaying = false
    private var isLoadingMoreUpcoming = false

    @Inject
    lateinit var presenter: HomeContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachView()
        initRecyclerViews()

        if (presenter.isFirstLoading()) {
            showNowPlayingProgressBar()
            showUpcomingProgressBar()
        }

        presenter.getMovies()
    }

    private fun attachView() {
        presenter.attach(this)
    }

    private fun initRecyclerViews() {
        initRecyclerView(binding.nowPlayingRecyclerView, this::loadMoreNowPlaying)
        initRecyclerView(binding.upcomingRecyclerView, this::loadMoreUpcoming)
    }

    private fun initRecyclerView(recyclerView: RecyclerView, onLoadMore: () -> Unit) {
        recyclerView.adapter = MovieListAdapter(
            false,
            this::navigateToMovieDetailFragment,
            this::changeMovieFavoriteState
        )
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (layoutManager.findLastVisibleItemPosition() >=
                    recyclerView.adapter?.itemCount?.minus(NUM_OF_ITEMS_FROM_LAST_ONE)!!
                ) {
                    onLoadMore.invoke()
                }
            }
        })
    }

    private fun loadMoreNowPlaying() {
        if (!isLoadingMoreNowPlaying) {
            isLoadingMoreNowPlaying = true
            presenter.loadMoreNowPlaying()
        }
    }

    private fun loadMoreUpcoming() {
        if (!isLoadingMoreUpcoming) {
            isLoadingMoreUpcoming = true
            presenter.loadMoreUpcoming()
        }
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showNowPlaying(nowPlayingMovies: List<MovieEntity>) {
        isLoadingMoreNowPlaying = false

        updateAdapterDataSet(
            binding.nowPlayingRecyclerView.adapter as MovieListAdapter,
            nowPlayingMovies
        )

        if (presenter.isFirstLoading()) {
            presenter.firstLoadingDone()
        }

        hideNowPlayingProgressBar()
    }

    override fun showUpcoming(upcomingMovies: List<MovieEntity>) {
        isLoadingMoreUpcoming = false

        updateAdapterDataSet(
            binding.upcomingRecyclerView.adapter as MovieListAdapter,
            upcomingMovies
        )

        if (presenter.isFirstLoading()) {
            presenter.firstLoadingDone()
        }

        hideUpcomingProgressBar()
    }

    override fun restoreNowPlayingRecyclerViewPosition(position: Parcelable) {
        binding.nowPlayingRecyclerView.layoutManager?.onRestoreInstanceState(position)
    }

    override fun restoreUpcomingRecyclerViewPosition(position: Parcelable) {
        binding.upcomingRecyclerView.layoutManager?.onRestoreInstanceState(position)
    }

    private fun updateAdapterDataSet(adapter: MovieListAdapter, data: List<MovieEntity>) {
        adapter.setData(data)
    }

    private fun navigateToMovieDetailFragment(movieId: Int) {
        saveRecyclerViewState()
        (requireActivity() as Contract).navigateToMovieDetailFragment(movieId)
    }

    private fun saveRecyclerViewState() {
        val nowPlayingRecyclerViewState =
            binding.nowPlayingRecyclerView.layoutManager?.onSaveInstanceState()
        val upcomingRecyclerViewState =
            binding.upcomingRecyclerView.layoutManager?.onSaveInstanceState()

        nowPlayingRecyclerViewState?.let {
            presenter.saveNowPlayingRecyclerViewPosition(it)
        }

        upcomingRecyclerViewState?.let {
            presenter.saveUpcomingRecyclerRecyclerViewPosition(it)
        }
    }

    private fun changeMovieFavoriteState(movieId: Int) {
        presenter.changeMovieFavoriteState(movieId)
    }

    private fun showNowPlayingProgressBar() {
        binding.nowPlayingProgressBar.isVisible = true
    }

    private fun hideNowPlayingProgressBar() {
        binding.nowPlayingProgressBar.isVisible = false
    }

    private fun showUpcomingProgressBar() {
        binding.upcomingProgressBar.isVisible = true
    }

    private fun hideUpcomingProgressBar() {
        binding.upcomingProgressBar.isVisible = false
    }

    override fun showOnLostConnectionMessage() {
        binding.noConnectionMessageLayout.isVisible = true
        hideNowPlayingProgressBar()
        hideUpcomingProgressBar()
    }

    override fun hideOnLostConnectionMessage() {
        binding.noConnectionMessageLayout.isVisible = false

        if (presenter.isFirstLoading()) {
            showNowPlayingProgressBar()
            showUpcomingProgressBar()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Contract) { "Activity must implement HomeFragment.Contract" }
    }

    interface Contract {
        fun navigateToMovieDetailFragment(movieId: Int)
    }
}