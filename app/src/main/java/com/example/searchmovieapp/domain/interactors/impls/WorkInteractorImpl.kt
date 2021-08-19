package com.example.searchmovieapp.domain.interactors.impls

import android.content.Context
import androidx.work.*
import com.example.searchmovieapp.R
import com.example.searchmovieapp.domain.NOTIFICATION_CONTENT_KEY
import com.example.searchmovieapp.domain.NOTIFICATION_IMAGE_PATH_KEY
import com.example.searchmovieapp.domain.NOTIFICATION_TITLE_KEY
import com.example.searchmovieapp.domain.NotificationWorker
import com.example.searchmovieapp.domain.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.interactors.WorkInteractor
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.sign

private const val DATE_FORMAT_PATTEN = "yyyy-MM-dd"

class WorkInteractorImpl @Inject constructor(private val applicationContext: Context) :
    WorkInteractor {

    private val workManager = WorkManager.getInstance(applicationContext)

    override fun isWorkRegistered(movieId: Int): Boolean {
        val status = workManager.getWorkInfosForUniqueWork(movieId.toString())

        val workInfoList = status.get()

        if (workInfoList.isEmpty()) return false

        val state = workInfoList.first().state

        return state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
    }

    override fun registerWork(movie: MovieEntity): Boolean {
        val timeToRelease = calculateTime(movie.releaseDate)

        if (timeToRelease.sign != 1) return false

        val dataBuilder = Data.Builder()

        dataBuilder.putString(NOTIFICATION_TITLE_KEY, movie.title)
        dataBuilder.putString(
            NOTIFICATION_CONTENT_KEY,
            applicationContext.getString(R.string.notification_content)
        )
        dataBuilder.putString(NOTIFICATION_IMAGE_PATH_KEY, movie.posterPath)

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(dataBuilder.build())
            .setInitialDelay(timeToRelease, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            movie.id.toString(),
            ExistingWorkPolicy.KEEP,
            notificationWorkRequest
        )

        return true
    }

    override fun unregisterWork(movie: MovieEntity) {
        workManager.cancelUniqueWork(movie.id.toString())
    }

    private fun calculateTime(date: String): Long {
        return try {
            val dateFormat: DateFormat = SimpleDateFormat(DATE_FORMAT_PATTEN, Locale.getDefault())
            val currentDate = Date()
            val releaseDate = dateFormat.parse(date)
            releaseDate!!.time - currentDate.time
        } catch (e: ParseException) {
            -1
        }
    }
}