package com.theotokossolutions.spiritualwarfare

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_options.*

class OptionsActivity : AppCompatActivity() {

    private var stopMusic = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        val handler = Handler()
        handler.postDelayed(Runnable { // Do something after 5s = 5000ms

            //START AUDIO
            if(!AudioPlayer.isPlaying) {
                AudioPlayer.resumeAudio()
            }
        }, 150)

        //ABOUT BUTTON
        about_button.setOnClickListener{
            stopMusic = false
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            finish()
        }

        //HOW TO PLAY BUTTON
        how_button.setOnClickListener{
            stopMusic = false
            val intent = Intent(this, HowToPlayActivity::class.java)
            startActivity(intent)
            finish()
        }

        //MUSIC BUTTON
        music_button.setOnClickListener{
            stopMusic = false
            val intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopMusic = false
        val intent = Intent(this, MainActivity::class.java)
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
