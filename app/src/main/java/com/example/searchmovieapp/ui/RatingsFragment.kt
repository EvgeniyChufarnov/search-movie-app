package com.example.searchmovieapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.searchmovieapp.adapters.MovieListAdapter
import com.example.searchmovieapp.contracts.RatingsContract
import com.example.searchmovieapp.databinding.FragmentRatingsBinding
import com.example.searchmovieapp.entities.MovieEntity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val COLUMNS_NUM = 3

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

        attachView()
        showProgressBar()
        initRecyclerView()
        presenter.getTopRatedMovies()
    }

    private fun attachView() {
        presenter.attach(this)
    }

    private fun initRecyclerView() {
        binding.moviesRecyclerView.adapter =
            MovieListAdapter(true, this::navigateToMovieDetailFragment)
        binding.moviesRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), COLUMNS_NUM, GridLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showMovies(movies: List<MovieEntity>) {
        updateAdapterDataSet(
            binding.moviesRecyclerView.adapter as MovieListAdapter,
            movies
        )
        hideProgressBar()
    }

    private fun updateAdapterDataSet(adapter: MovieListAdapter, data: List<MovieEntity>) {
        adapter.setData(data)
    }

    private fun showProgressBar() {
        binding.loadingLayout.isVisible = true
    }

    private fun hideProgressBar() {
        binding.loadingLayout.isVisible = false
    }

    private fun navigateToMovieDetailFragment(movieId: Int) {
        (requireActivity() as HomeFragment.Contract).navigateToMovieDetailFragment(movieId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Contract) { "Activity must implement RatingsFragment.Contract" }
    }

    interface Contract {
        fun navigateToMovieDetailFragment(movieId: Int)
    }
}