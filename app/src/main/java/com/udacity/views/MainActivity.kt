package com.udacity.views

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import com.udacity.model.Download
import com.udacity.notifications.NotificationHelper
import com.udacity.views.customViews.ButtonState
import com.udacity.views.customViews.LoadingButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var loadingButton : LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton = custom_button!!
        custom_button!!.setOnClickListener {
            when(packages_radioGroup.checkedRadioButtonId){
                R.id.glide_radioButton -> download(GLIDE_PACKAGE)
                R.id.loadApp_radioButton -> download(LOADAPP_PACKAGE)
                R.id.retrofit_radioButton -> download(RETROFIT_PACKAGE)
                else -> Toast.makeText(
                    applicationContext,
                    applicationContext.getString(R.string.no_package_selected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            loadingButton.buttonState = ButtonState.Completed // Update the button

            /* Download */
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val query = DownloadManager.Query()
            query.setFilterById(id!!)
            val downloadManager = context!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                if (cursor.count > 0) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                    // Select downloaded package
                    var downloadedPackage : Download = Download()
                    when(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))){
                        GLIDE_PACKAGE.name -> {
                            downloadedPackage = GLIDE_PACKAGE
                            downloadedPackage.fullName = context.getString(R.string.glide_label)
                        }
                        LOADAPP_PACKAGE.name -> {
                            downloadedPackage = LOADAPP_PACKAGE
                            downloadedPackage.fullName = context.getString(R.string.loadapp_label)
                        }
                        RETROFIT_PACKAGE.name -> {
                            downloadedPackage = RETROFIT_PACKAGE
                            downloadedPackage.fullName = context.getString(R.string.retrofit_label)
                        }
                    }

                    // Result of the download
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadedPackage.status = true
                        Toast.makeText(
                            context,
                            context.getString(R.string.notification_download_success).replace("{REPOSITORY}", downloadedPackage.name),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.notification_download_failure)
                                .replace("{REPOSITORY}", downloadedPackage.name),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    /* Send a notification */
                    NotificationHelper(application).createNotification(downloadedPackage)
                }
            }
        }

    }

    private fun download(toDownload : Download) {
        val request =
            DownloadManager.Request(Uri.parse(toDownload.url))
                .setTitle(toDownload.name)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        loadingButton.buttonState = ButtonState.Loading // Update button
    }

    companion object {
        private val GLIDE_PACKAGE = Download(name = "Glide",
            url = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
            )
        private val LOADAPP_PACKAGE = Download(name = "LoadApp",
            url = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
            )
        private val RETROFIT_PACKAGE = Download(name = "Retrofit",
            url = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        )

        private const val CHANNEL_ID = "channelId"
    }

}