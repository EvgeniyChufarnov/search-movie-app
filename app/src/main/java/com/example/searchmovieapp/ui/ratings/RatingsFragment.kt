package com.example.searchmovieapp.ui.ratings

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.searchmovieapp.databinding.FragmentRatingsBinding
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.ui.common.MovieListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val COLUMNS_NUM = 3
private const val NUM_OF_ITEMS_FROM_LAST_ONE = 5

@AndroidEntryPoint
class RatingsFragment : Fragment(), RatingsContract.View {
    private var _binding: FragmentRatingsBinding? = null
    private val binding get() = _binding!!
    private var isLoadingMore = false

    @Inject
    lateinit var presenter: RatingsContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachView()
        initRecyclerView()

        if (presenter.isFirstLoading()) {
            showProgressBar()
        }

        presenter.getTopRatedMovies()
    }

    private fun attachView() {
        presenter.attach(this)
    }

    private fun initRecyclerView() {
        binding.moviesRecyclerView.adapter =
            MovieListAdapter(
                true,
                this::navigateToMovieDetailFragment,
                this::changeMovieFavoriteState
            )

        val layoutManager =
            GridLayoutManager(requireContext(), COLUMNS_NUM, GridLayoutManager.VERTICAL, false)
        binding.moviesRecyclerView.layoutManager = layoutManager

        binding.moviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (layoutManager.findLastVisibleItemPosition() >=
                    recyclerView.adapter?.itemCount?.minus(NUM_OF_ITEMS_FROM_LAST_ONE)!!
                ) {
                    loadMore()
                }
            }
        })
    }

    private fun loadMore() {
        if (!isLoadingMore) {
            isLoadingMore = true
            presenter.loadMore()
        }
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showMovies(movies: List<MovieEntity>) {
        isLoadingMore = false
        updateAdapterDataSet(
            binding.moviesRecyclerView.adapter as MovieListAdapter,
            movies
        )

        if (presenter.isFirstLoading()) {
            hideProgressBar()
            presenter.firstLoadingDone()
        }
    }

    override fun restoreRecyclerViewPosition(position: Parcelable) {
        binding.moviesRecyclerView.layoutManager?.onRestoreInstanceState(position)
    }

    private fun updateAdapterDataSet(adapter: MovieListAdapter, data: List<MovieEntity>) {
        adapter.setData(data)
    }

    private fun showProgressBar() {
        binding.loadingProcessBar.isVisible = true
    }

    private fun hideProgressBar() {
        binding.loadingProcessBar.isVisible = false
    }

    private fun navigateToMovieDetailFragment(movieId: Int) {
        saveRecyclerViewState()
        (requireActivity() as Contract).navigateToMovieDetailFragment(movieId)
    }

    private fun saveRecyclerViewState() {
        val recyclerViewState = binding.moviesRecyclerView.layoutManager?.onSaveInstanceState()
        recyclerViewState?.let {
            presenter.saveRecyclerViewPosition(it)
        }
    }

    private fun changeMovieFavoriteState(movieId: Int) {
        presenter.changeMovieFavoriteState(movieId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Contract) { "Activity must implement RatingsFragment.Contract" }
    }

    interface Contract {
        fun navigateToMovieDetailFragment(movieId: Int)
    }
}