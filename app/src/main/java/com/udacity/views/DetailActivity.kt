package com.udacity.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import com.udacity.R
import com.udacity.databinding.ContentDetailBinding
import com.udacity.model.Download
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.toolbar
import kotlinx.android.synthetic.main.activity_main.*

class DetailActivity : AppCompatActivity() {

    private lateinit var motionLayout : MotionLayout
    private var backButtonPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        motionLayout = findViewById(R.id.detail_motionLayout)

        val obtainedData = intent.extras

        val bind = DataBindingUtil.bind<ContentDetailBinding>(contentDetail_fragment)
        bind?.file =  Download(
            name = obtainedData?.getString("download_name")!!,
            status = obtainedData.getBoolean("download_status"))

        fab.setOnClickListener {
            backButtonPressed = true
            motionLayout?.transitionToStart()
            Log.d("MOTION", "EXECUTED")
        }

        motionLayout?.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                TODO("Not yet implemented")
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                TODO("Not yet implemented")
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                Log.d("MOTION", "INTRO")
                if(backButtonPressed){
                    Log.d("MOTION", "EXIT")
                    startActivity(Intent(application, MainActivity::class.java))
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                TODO("Not yet implemented")
            }
        })

    }

}
