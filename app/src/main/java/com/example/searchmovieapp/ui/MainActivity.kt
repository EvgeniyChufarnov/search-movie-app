package com.example.searchmovieapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.searchmovieapp.R
import com.example.searchmovieapp.domain.ConnectionState
import com.example.searchmovieapp.ui.details.MovieDetailsFragment
import com.example.searchmovieapp.ui.favorites.FavoritesFragment
import com.example.searchmovieapp.ui.home.HomeFragment
import com.example.searchmovieapp.ui.ratings.RatingsFragment
import com.example.searchmovieapp.util.MOVIE_NOTIFICATION_CHANNEL_ID
import com.example.searchmovieapp.util.PUSH_CHANNEL_ID
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HomeFragment.Contract, FavoritesFragment.Contract,
    RatingsFragment.Contract {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFullScreenContainer()
        ConnectionState.setContext(this)
        tryToCreateNotificationChannel()
    }

    private fun initFullScreenContainer() {
        supportFragmentManager.beginTransaction()
            .add(R.id.full_screen_container, NavigationContainerFragment())
            .commit()
    }

    private fun tryToCreateNotificationChannel() {
        val notificationManager = NotificationManagerCompat.from(this)

        val pushNotificationChannel =
            NotificationChannelCompat.Builder(PUSH_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName(getString(R.string.push_notification_channel_name))
                .setDescription(getString(R.string.push_notification_channel_description))
                .build()

        val movieNotificationChannel =
            NotificationChannelCompat.Builder(MOVIE_NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName(getString(R.string.movie_notification_channel))
                .setDescription(getString(R.string.movie_notification_channel_description))
                .build()

        notificationManager.createNotificationChannel(pushNotificationChannel)
        notificationManager.createNotificationChannel(movieNotificationChannel)
    }


    override fun navigateToMovieDetailFragment(movieId: Int) {
        val detailMovieFragment = MovieDetailsFragment.getInstance(movieId)

        supportFragmentManager.beginTransaction()
            .replace(R.id.full_screen_container, detailMovieFragment)
            .addToBackStack(null)
            .commit()
    }
}