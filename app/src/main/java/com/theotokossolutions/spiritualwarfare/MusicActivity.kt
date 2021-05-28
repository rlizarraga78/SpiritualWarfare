package com.theotokossolutions.spiritualwarfare

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_music.*

class MusicActivity : AppCompatActivity() {

    private var stopMusic = true
    private var musicChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR)

        music_button_1.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.intro_theme_music)
            musicChange = true
        }

        music_button_2.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.guardian_angel_music)
            musicChange = true
        }

        music_button_3.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.annunciation_music)
            musicChange = true
        }

        music_button_4.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.nativity_music)
            musicChange = true
        }

        music_button_5.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.agni_parthene_music)
            musicChange = true
        }

        music_button_6.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.theophany_troparion_music)
            musicChange = true
        }

        music_button_7.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.psalm_135_music)
            musicChange = true
        }

        music_button_8.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.kyrie_eleison_music)
            musicChange = true
        }

        music_button_9.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.christos_anesti_music)
            musicChange = true
        }

        music_button_10.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.cherubic_hymn_music)
            musicChange = true
        }

        music_button_11.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.ladder_to_heaven_music)
            musicChange = true
        }

        music_button_12.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.the_day_of_judgement_music)
            musicChange = true
        }

        music_button_13.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.the_high_priest_music)
            musicChange = true
        }

        music_button_14.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.win_theme_music)
            musicChange = true
        }

        music_button_15.setOnClickListener{
            AudioPlayer.changeSong(this, R.raw.eternal_rest_music)
            musicChange = true
        }

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
        if(musicChange) AudioPlayer.changeSong(this, R.raw.intro_theme_music)
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