package com.example.searchmovieapp.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.searchmovieapp.R
import com.example.searchmovieapp.databinding.ItemNowPlayingBinding
import com.example.searchmovieapp.databinding.ItemUpcomingBinding
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.repositories.isFavorite

private const val NOW_PLAYING_MOVIE_TYPE = 0
private const val UPCOMING_MOVIE_TYPE = 1

private const val INDEX_OF_LAST_YEAR_CHAR = 3
private val MovieEntity.releaseYear: String
    get() = releaseDate.slice(0..INDEX_OF_LAST_YEAR_CHAR)

class MovieListAdapter(
    private val isLayoutManageVertical: Boolean,
    private val onMovieClicked: (Int) -> Unit,
    private val onFavoriteClicked: (Int) -> Unit
) :
    RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

    private var dataSet: List<MovieEntity> = emptyList()

    fun setData(movies: List<MovieEntity>) {
        dataSet = movies
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position].isUpcoming) UPCOMING_MOVIE_TYPE else NOW_PLAYING_MOVIE_TYPE
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = when (viewType) {
        NOW_PLAYING_MOVIE_TYPE -> NowPlayingMovieViewHolder(viewGroup)
        UPCOMING_MOVIE_TYPE -> UpcomingMovieViewHolder(viewGroup)
        else -> throw RuntimeException("Unknown view type")
    }

    override fun onBindViewHolder(viewHolder: MovieViewHolder, position: Int) {
        viewHolder.bind(dataSet[position], isLayoutManageVertical, onMovieClicked, onFavoriteClicked)
    }

    override fun getItemCount() = dataSet.size

    sealed class MovieViewHolder(viewGroup: ViewGroup, itemId: Int) : RecyclerView.ViewHolder(
        LayoutInflater.from(viewGroup.context).inflate(itemId, viewGroup, false)
    ) {
        abstract fun bind(
            movie: MovieEntity,
            isLayoutManageVertical: Boolean,
            onMovieClicked: (Int) -> Unit,
            onFavoriteClicked: (Int) -> Unit
        )
    }

    class NowPlayingMovieViewHolder(viewGroup: ViewGroup, itemId: Int = R.layout.item_now_playing) :
        MovieViewHolder(viewGroup, itemId) {
        private val binding = ItemNowPlayingBinding.bind(itemView)

        override fun bind(
            movie: MovieEntity,
            isLayoutManageVertical: Boolean,
            onMovieClicked: (Int) -> Unit,
            onFavoriteClicked: (Int) -> Unit
        ) {
            if (isLayoutManageVertical) {
                switchToConstraintsForVerticalLayoutManager()
            }

            setText(movie)
            setPoster(movie.posterPath)
            changeFavoriteButtonImage(movie.isFavorite())

            itemView.setOnClickListener {
                onMovieClicked.invoke(movie.id)
            }

            binding.setFavoriteImageView.setOnClickListener {
                onFavoriteClicked.invoke(movie.id)
                changeFavoriteButtonImage(movie.isFavorite())
            }
        }

        private fun setText(movie: MovieEntity) {
            binding.titleTextView.text = movie.title
            binding.ratingTextView.text = movie.voteAverage.toString()
            binding.yearTextView.text = movie.releaseYear
        }

        private fun setPoster(path: String?) {
            if (path == null) {
                binding.posterImageView.setImageResource(R.drawable.default_movie_poster)
            } else {
                Glide.with(binding.root.context).load(path).into(binding.posterImageView)
            }
        }

        private fun changeFavoriteButtonImage(isFavorite: Boolean) {
            binding.setFavoriteImageView.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
        }

        private fun switchToConstraintsForVerticalLayoutManager() {
            binding.containerCardView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            binding.containerCardView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.containerLayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            binding.containerLayout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    class UpcomingMovieViewHolder(viewGroup: ViewGroup, itemId: Int = R.layout.item_upcoming) :
        MovieViewHolder(viewGroup, itemId) {
        private val binding = ItemUpcomingBinding.bind(itemView)

        override fun bind(
            movie: MovieEntity,
            isLayoutManageVertical: Boolean,
            onMovieClicked: (Int) -> Unit,
            onFavoriteClicked: (Int) -> Unit
        ) {
            if (isLayoutManageVertical) {
                switchToConstraintsForVerticalLayoutManager()
            }

            setText(movie)
            setPoster(movie.posterPath)
            changeFavoriteButtonImage(movie.isFavorite())

            itemView.setOnClickListener {
                onMovieClicked(movie.id)
            }

            binding.setFavoriteImageView.setOnClickListener {
                onFavoriteClicked(movie.id)
                changeFavoriteButtonImage(movie.isFavorite())
            }
        }

        private fun setText(movie: MovieEntity) {
            binding.titleTextView.text = movie.title
            binding.yearTextView.text = movie.releaseDate
        }

        private fun setPoster(path: String?) {
            if (path == null) {
                binding.posterImageView.setImageResource(R.drawable.default_movie_poster)
            } else {
                Glide.with(binding.root.context).load(path).into(binding.posterImageView)
            }
        }

        private fun changeFavoriteButtonImage(isFavorite: Boolean) {
            binding.setFavoriteImageView.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
        }

        private fun switchToConstraintsForVerticalLayoutManager() {
            binding.containerCardView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            binding.containerCardView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.containerLayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            binding.containerLayout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
}