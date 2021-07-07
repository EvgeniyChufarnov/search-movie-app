package com.example.searchmovieapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.searchmovieapp.adapters.MovieListAdapter
import com.example.searchmovieapp.contracts.HomeScreenContract
import com.example.searchmovieapp.databinding.FragmentHomeBinding
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.injection.MovieApplication


class HomeFragment : Fragment(),
    HomeScreenContract.View {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: HomeScreenContract.Presenter

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

        createPresenter()
        attachView()
        initRecyclerViews()
    }

    private fun createPresenter() {
        val appContainer = (requireActivity().application as MovieApplication).appContainer
        presenter = appContainer.homePresenterFactory.create()
    }

    private fun attachView() {
        presenter.attach(this)
    }

    private fun initRecyclerViews() {
        initRecyclerView(binding.nowPlayingRecyclerView)
        initRecyclerView(binding.upcomingRecyclerView)
        presenter.getMovies()
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = MovieListAdapter(this::navigateToMovieDetailFragment)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showNowPlaying(nowPlayingMovies: List<MovieEntity>) {
        updateAdapterDataSet(
            binding.nowPlayingRecyclerView.adapter as MovieListAdapter,
            nowPlayingMovies
        )
    }

    override fun showUpcoming(upcomingMovies: List<MovieEntity>) {
        updateAdapterDataSet(
            binding.upcomingRecyclerView.adapter as MovieListAdapter,
            upcomingMovies
        )
    }

    private fun updateAdapterDataSet(adapter: MovieListAdapter, data: List<MovieEntity>) {
        adapter.setData(data)
        adapter.notifyDataSetChanged()
    }

    fun navigateToMovieDetailFragment(movieId: Int) {
        (requireActivity() as Contract).navigateToMovieDetailFragment(movieId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Contract) { "Activity must implement HomeFragment.Contract" }
    }

    interface Contract {
        fun navigateToMovieDetailFragment(movieId: Int)
    }
}