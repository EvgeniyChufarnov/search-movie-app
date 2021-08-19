package com.example.searchmovieapp.ui.ratings

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.searchmovieapp.R
import com.example.searchmovieapp.domain.data.remote.entities.MovieEntity
import com.example.searchmovieapp.databinding.FragmentRatingsBinding
import com.example.searchmovieapp.ui.common.MovieListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val COLUMNS_NUM = 3
private const val NUM_OF_ITEMS_FROM_LAST_ONE = 5

@AndroidEntryPoint
class RatingsFragment : Fragment(), RatingsContract.View {
    private var _binding: FragmentRatingsBinding? = null
    private val binding get() = _binding!!

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

        initRecyclerView()
        presenter.attach(this)
    }

    private fun initRecyclerView() {
        binding.moviesRecyclerView.adapter =
            MovieListAdapter(
                true,
                presenter::navigateToMovieDetailFragment,
                presenter::changeMovieFavoriteState
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
                    presenter.loadMore()
                }
            }
        })
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showMovies(movies: List<MovieEntity>) {
        (binding.moviesRecyclerView.adapter as MovieListAdapter).setData(movies)
    }

    override fun showMoreMovies(movies: List<MovieEntity>) {
        (binding.moviesRecyclerView.adapter as MovieListAdapter).addData(movies)
    }

    override fun restoreRecyclerViewPosition(position: Parcelable) {
        binding.moviesRecyclerView.layoutManager?.onRestoreInstanceState(position)
    }

    override fun showProgressBar() {
        binding.loadingProcessBar.isVisible = true
    }

    override fun hideProgressBar() {
        binding.loadingProcessBar.isVisible = false
    }

    override fun showOnLostConnectionMessage() {
        binding.noConnectionMessageLayout.isVisible = true
    }

    override fun hideOnLostConnectionMessage() {
        binding.noConnectionMessageLayout.isVisible = false
    }

    override fun showConnectionError(message: String?) {
        val errorMessage = message ?: getString(R.string.default_network_error)
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToMovieDetailFragment(movieId: Int) {
        (requireActivity() as Contract).navigateToMovieDetailFragment(movieId)
    }

    override fun getRecyclerViewState(): Parcelable? =
        binding.moviesRecyclerView.layoutManager?.onSaveInstanceState()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Contract) { "Activity must implement RatingsFragment.Contract" }
    }

    interface Contract {
        fun navigateToMovieDetailFragment(movieId: Int)
    }
}