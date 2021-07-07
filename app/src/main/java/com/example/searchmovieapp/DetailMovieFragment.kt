package com.example.searchmovieapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.searchmovieapp.contracts.MovieDetailsContract
import com.example.searchmovieapp.databinding.FragmentMovieDetailsBinding
import com.example.searchmovieapp.entities.MovieDetailsEntity
import com.example.searchmovieapp.injection.MovieApplication
import kotlin.properties.Delegates

private const val MOVIE_ID_EXTRA_KEY = "movie id"

class DetailMovieFragment : Fragment(), MovieDetailsContract.View {
    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: MovieDetailsContract.Presenter

    private var movieId by Delegates.notNull<Int>()

    companion object Instance {
        fun getInstance(movieId: Int): DetailMovieFragment {
            val detailMovieFragment = DetailMovieFragment()
            val bundle = Bundle()
            bundle.putInt(MOVIE_ID_EXTRA_KEY, movieId)
            detailMovieFragment.arguments = bundle
            return detailMovieFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMovieIdFromArguments()
        createPresenter()
        attachView()
        hideUi()
        requestMovieDetails()
    }

    private fun getMovieIdFromArguments() {
        arguments?.run {
            if (containsKey(MOVIE_ID_EXTRA_KEY)) {
                movieId = getInt(MOVIE_ID_EXTRA_KEY)
            }
        }
    }

    private fun createPresenter() {
        val appContainer = (requireActivity().application as MovieApplication).appContainer
        presenter = appContainer.movieDetailsPresenterFactory.create()
    }

    private fun attachView() {
        presenter.attach(this)
    }

    private fun hideUi() {
        binding.starImageView.visibility = View.GONE
        binding.setFavoriteImageView.visibility = View.GONE
    }

    private fun requestMovieDetails() {
        presenter.getMovieDetails(movieId)
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showDetails(movieDetails: MovieDetailsEntity) {
        with(movieDetails) {
            binding.mainTitleTextView.text = title
            binding.originalTitleTextView.text = originalTitle
            binding.genresTextView.text = genres.joinToString(", ")
            binding.budgetTextView.text = getString(R.string.budget, budget)
            binding.revenueTextView.text = getString(R.string.revenue, revenue)
            binding.releaseDateTextView.text = getString(R.string.release_date, releaseDate)
            binding.descriptionTextView.text = overview
            binding.ratingsTextView.text = getString(
                R.string.ratings_with_vote_count,
                voteAverage, voteCount
            )

            movieDetails.runtime?.let {
                binding.durationTextView.text = getString(R.string.duration, runtime)
            }
        }

        showUI()
        setPoster(movieDetails.posterPath)
    }

    private fun showUI() {
        binding.starImageView.visibility = View.VISIBLE
        binding.setFavoriteImageView.visibility = View.VISIBLE
    }

    private fun setPoster(path: String?) {
        if (path == null) {
            binding.posterImageView.setImageResource(R.drawable.default_movie_poster)
        } else {
            //todo set image via glide
        }
    }
}