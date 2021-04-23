package com.udacity.views

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import com.udacity.BR
import com.udacity.R
import com.udacity.databinding.ContentDetailBinding
import com.udacity.model.Download
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.toolbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var motionLayout : MotionLayout
    private var backButtonPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        motionLayout = ContentDetailBinding.bind(findViewById(R.id.contentDetail_fragment)).detailMotionLayout

        cancelNotification(intent.getIntExtra("notification_id", 10))

        val bind = DataBindingUtil.getBinding<ContentDetailBinding>(contentDetail_fragment)
        bind?.setVariable(BR.file, Download(
                fullName = intent.getStringExtra("download_name")!!,
                status = intent.getBooleanExtra("download_status", false))
        )

        fab.setOnClickListener {
            backButtonPressed = true
            motionLayout.transitionToStart()
        }

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if (backButtonPressed) {
                    startActivity(Intent(application, MainActivity::class.java))
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })

    }

    private fun cancelNotification(notificationId: Int) {
        (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE ) as NotificationManager).cancel(notificationId)
    }

}
