package com.theotokossolutions.spiritualwarfare

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_video.*


class VideoActivity : AppCompatActivity() {

    private var level : Int = 0
    var goToNextLevel = true
    var pause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR)

        //GET PREFERENCES
        val prefs = this.getSharedPreferences("DATA", 0)
        var editor = prefs.edit()
        level = prefs.getInt("level", -1)


        //SELECT VIDEO
        when(level) {
            1 -> levelView.setImageResource(R.drawable.level_1_start)
            2 -> levelView.setImageResource(R.drawable.level_2_start)
            3 -> levelView.setImageResource(R.drawable.level_3_start)
            4 -> levelView.setImageResource(R.drawable.level_4_start)
            5 -> levelView.setImageResource(R.drawable.level_5_start)
            6 -> levelView.setImageResource(R.drawable.level_6_start)
            7 -> levelView.setImageResource(R.drawable.level_7_start)
            8 -> levelView.setImageResource(R.drawable.level_8_start)
            9 -> levelView.setImageResource(R.drawable.level_9_start)
            10 -> levelView.setImageResource(R.drawable.level_10_start)
            11-> levelView.setImageResource(R.drawable.level_11_start)
            12-> levelView.setImageResource(R.drawable.level_12_start)
            13 -> {
                levelView.setImageResource(R.drawable.you_win)
                val handler = Handler()
                handler.postDelayed(Runnable {

                    //START AUDIO
                    if(!AudioPlayer.isPlaying || AudioPlayer.isStopped) {
                        AudioPlayer.changeSong(this, R.raw.win_theme_music)
                    }
                }, 1000)
            }
            else -> {
                editor.putInt("level", 1)
                editor.apply()
            }
        }

        var red = 240
        var green = 0
        var blue = 0
        var count : Int

        if(level == 13)  count = 300
        else count = 35


        levelView.setColorFilter(Color.argb(150, red, green, blue))

            // Create the Handler object (on the main thread by default)
            val handler = Handler()
            val runnableCode: Runnable = object : Runnable {
                override fun run() {
                    if(!pause) {
                        levelView.setColorFilter(Color.argb(150, red, green, blue))
                        if (red == 240 && green < 240) green += 30
                        if (green == 240 && red > 0) red -= 30
                        if (red == 0 && blue < 240) blue += 30
                        if (blue == 240 && green > 0) green -= 30
                        if (green == 0 && blue == 240) red += 30
                        if (red == 240 && blue > 0) blue -= 30

                        count--
                        if (count > 0) handler.postDelayed(this, 10)
                        else if (count <= 0 && goToNextLevel) startLevel()
                    }
                    else handler.postDelayed(this, 100)
                }
            }
            handler.post(runnableCode)

        val handler2 = Handler()
        handler2.postDelayed(Runnable {

            //STOP AUDIO
            if(AudioPlayer.isPlaying) {
                AudioPlayer.pauseAudio()
            }
        }, 150)
    }

    override fun onBackPressed() {
        AudioPlayer.changeSong(this, R.raw.intro_theme_music)
        AudioPlayer.isPlaying = false
        AudioPlayer.isStopped = true
        AudioPlayer.pauseAudio()
        pause = true
        goToNextLevel = false
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStop() {
        super.onStop()
        //releasePlayer()
        pause = true
        if(level == 13 && !pause) {
            AudioPlayer.pauseAudio()
        }
    }

    override fun onRestart() {
        super.onRestart()
        pause = false
        if(level == 13) {
            AudioPlayer.resumeAudio()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            ///video_view.pause()
        }
    }

    private fun releasePlayer() {
        //video_view.stopPlayback()
    }

    private fun startLevel(){
        pause = true
        AudioPlayer.isPlaying = false
        val intent: Intent = if(level == 13) Intent(this, MainActivity::class.java)
        else Intent(this, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

}
