package com.udacity.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.udacity.R
import com.udacity.model.Download
import com.udacity.views.DetailActivity
import com.udacity.views.MainActivity

class NotificationHelper(private val context: Context) {

    private val CHANNEL_ID = "downloadNotificationId"
    private val NOTIFICATION_ID = 10

    fun createNotification(download: Download){
        createNotificationChannel()

        /* Intents */
        val intentToMain = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK}
        val pendingIntentToMain = PendingIntent.getActivity(context, 0, intentToMain, 0)

        Log.d("DOWN CONTENT", download.fullName)
        val intentToDetails = Intent(context, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("download_name", download.fullName)
            putExtra("download_status", download.status)
        }
        val pendingIntentToDetails = PendingIntent.getActivity(context, 0, intentToDetails, PendingIntent.FLAG_UPDATE_CURRENT)

        /* Notification */
        val notificationText : String =
            when(download.status){
                true -> context.getString(R.string.notification_download_success)
                else -> context.getString(R.string.notification_download_failure)
            }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(
                notificationText.replace("{REPOSITORY}", download.name)
            )
            .setContentIntent(pendingIntentToMain)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                R.drawable.notification_icon,
                context.getString(R.string.notification_button),
                pendingIntentToDetails
            )
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Packages download notificator",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}