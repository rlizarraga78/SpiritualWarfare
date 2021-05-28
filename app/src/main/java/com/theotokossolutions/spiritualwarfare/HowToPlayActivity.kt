package com.theotokossolutions.spiritualwarfare

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_how.*

class HowToPlayActivity : AppCompatActivity(){

    private var stopMusic = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR)

        how_to_image_1.setBackgroundResource(R.drawable.boy_up_animation)
        val bgImage = how_to_image_1.background as AnimationDrawable
        bgImage.start()

        val handler = Handler()
        handler.postDelayed(Runnable { // Do something after 5s = 5000ms

            //START AUDIO
            if(!AudioPlayer.isPlaying) {
                AudioPlayer.resumeAudio()
            }
        }, 150)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopMusic = false
        val intent = Intent(this, OptionsActivity::class.java)
        intent.putExtra("MUSIC", "TRUE");
        startActivity(intent)
        finish()
    }

    override fun onStop() {
        super.onStop()
        if(stopMusic) AudioPlayer.pauseAudio()
    }

    override fun onRestart() {
        super.onRestart()
        AudioPlayer.resumeAudio()
    }
}