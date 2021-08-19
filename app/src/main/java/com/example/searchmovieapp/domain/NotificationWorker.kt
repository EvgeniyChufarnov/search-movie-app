package com.example.searchmovieapp.domain

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.searchmovieapp.R
import com.example.searchmovieapp.ui.MainActivity
import com.example.searchmovieapp.util.MOVIE_NOTIFICATION_CHANNEL_ID
import com.example.searchmovieapp.util.PUSH_CHANNEL_ID

const val NOTIFICATION_TITLE_KEY = "notification title"
const val NOTIFICATION_CONTENT_KEY = "notification content"
const val NOTIFICATION_IMAGE_PATH_KEY = "notification image path"
private const val NOTIFICATION_ID = 10
private const val REQUEST_CODE = 0

class NotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            appContext, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, PUSH_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(inputData.getString(NOTIFICATION_TITLE_KEY))
            .setContentText(inputData.getString(NOTIFICATION_CONTENT_KEY))
            .setChannelId(MOVIE_NOTIFICATION_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(appContext)

        Glide.with(appContext)
            .asBitmap()
            .load(inputData.getString(NOTIFICATION_IMAGE_PATH_KEY))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    notification.setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(resource))

                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                }
            })
    }
}