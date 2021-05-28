package com.theotokossolutions.spiritualwarfare

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_character_screen.*


class CharacterScreenActivity : AppCompatActivity() {

    private var boySelected = false
    private var girlSelected = false
    private var michaelSelected = false
    private var gabrielSelected = false
    private var raphaelSelected = false
    private var urielSelected = false
    private var startButtonReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_screen)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR)

        lets_start_button.setBackgroundColor(Color.parseColor("#77ffbb33"))
        lets_start_button.setTextColor(Color.parseColor("#77000000"))

        boy_icon_button.setColorFilter(Color.argb(144, 144, 144,144))
        girl_icon_button.setColorFilter(Color.argb(144, 144, 144,144))
        michael_icon.setColorFilter(Color.argb(144, 144, 144,144))
        gabriel_icon.setColorFilter(Color.argb(144, 144, 144,144))
        raphael_icon.setColorFilter(Color.argb(144, 144, 144,144))
        uriel_icon.setColorFilter(Color.argb(144, 144, 144,144))
        angel_stats_name.text = ""

        boy_icon_button.setOnClickListener{
            boy_icon_button.setColorFilter(Color.argb(0, 144, 144,144))
            girl_icon_button.setColorFilter(Color.argb(144, 144, 144,144))
            boySelected = true
            girlSelected = false
            checkStartButton()
        }

        girl_icon_button.setOnClickListener{
            boy_icon_button.setColorFilter(Color.argb(144, 144, 144,144))
            girl_icon_button.setColorFilter(Color.argb(0, 144, 144,144))
            boySelected = false
            girlSelected = true
            checkStartButton()
        }

        michael_icon.setOnClickListener{
            michael_icon.setColorFilter(Color.argb(0, 144, 144,144))
            gabriel_icon.setColorFilter(Color.argb(144, 144, 144,144))
            raphael_icon.setColorFilter(Color.argb(144, 144, 144,144))
            uriel_icon.setColorFilter(Color.argb(144, 144, 144,144))
            angel_stats_name.text = getString(R.string.michael_name)
            angel_stats_attack_header.text = getString(R.string.attack_speed_string)
            angel_stats_attack_speed.text = getString(R.string.fast_string)
            angel_stats_shield_header.text = getString(R.string.shield_recovery_string)
            angel_stats_shield_recovery.text = getString(R.string.fast_string)
            michaelSelected = true
            gabrielSelected = false
            raphaelSelected = false
            urielSelected = false
            checkStartButton()
        }

        gabriel_icon.setOnClickListener{
            michael_icon.setColorFilter(Color.argb(144, 144, 144,144))
            gabriel_icon.setColorFilter(Color.argb(0, 144, 144,144))
            raphael_icon.setColorFilter(Color.argb(144, 144, 144,144))
            uriel_icon.setColorFilter(Color.argb(144, 144, 144,144))
            angel_stats_name.text = getString(R.string.gabriel_name)
            angel_stats_attack_header.text = getString(R.string.attack_speed_string)
            angel_stats_attack_speed.text = getString(R.string.fast_string)
            angel_stats_shield_header.text = getString(R.string.shield_recovery_string)
            angel_stats_shield_recovery.text = getString(R.string.slow_string)
            michaelSelected = false
            gabrielSelected = true
            raphaelSelected = false
            urielSelected = false
            checkStartButton()
        }

        raphael_icon.setOnClickListener{
            michael_icon.setColorFilter(Color.argb(144, 144, 144,144))
            gabriel_icon.setColorFilter(Color.argb(144, 144, 144,144))
            raphael_icon.setColorFilter(Color.argb(0, 144, 144,144))
            uriel_icon.setColorFilter(Color.argb(144, 144, 144,144))
            angel_stats_name.text = getString(R.string.raphael_name)
            angel_stats_attack_header.text = getString(R.string.attack_speed_string)
            angel_stats_attack_speed.text = getString(R.string.slow_string)
            angel_stats_shield_header.text = getString(R.string.shield_recovery_string)
            angel_stats_shield_recovery.text = getString(R.string.fast_string)
            michaelSelected = false
            gabrielSelected = false
            raphaelSelected = true
            urielSelected = false
            checkStartButton()
        }

        uriel_icon.setOnClickListener{
            michael_icon.setColorFilter(Color.argb(144, 144, 144,144))
            gabriel_icon.setColorFilter(Color.argb(144, 144, 144,144))
            raphael_icon.setColorFilter(Color.argb(144, 144, 144,144))
            uriel_icon.setColorFilter(Color.argb(0, 144, 144,144))
            angel_stats_name.text = getString(R.string.uriel_name)
            angel_stats_attack_header.text = getString(R.string.attack_speed_string)
            angel_stats_attack_speed.text = getString(R.string.slow_string)
            angel_stats_shield_header.text = getString(R.string.shield_recovery_string)
            angel_stats_shield_recovery.text = getString(R.string.slow_string)
            michaelSelected = false
            gabrielSelected = false
            raphaelSelected = false
            urielSelected = true
            checkStartButton()
        }

        lets_start_button.setOnClickListener{
            if(startButtonReady){
                val prefs = this.getSharedPreferences("DATA", 0)
                var editor = prefs.edit()

                if(boySelected)
                    editor.putInt("human", 1)
                else if(girlSelected)
                    editor.putInt("human", 2)

                if(michaelSelected)
                    editor.putInt("angel", 1)
                else if(gabrielSelected)
                    editor.putInt("angel", 2)
                else if(raphaelSelected)
                    editor.putInt("angel", 3)
                else if(urielSelected)
                    editor.putInt("angel", 4)

                editor.putInt("level", 1)
                editor.apply()

                AudioPlayer.pauseAudio()
                val intent = Intent(this, VideoActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val handler = Handler()
        handler.postDelayed(Runnable { // Do something after 5s = 5000ms

            //START AUDIO
            if(!AudioPlayer.isPlaying) {
                AudioPlayer.resumeAudio()
            }
        }, 150)
    }

    private fun checkStartButton() {
        if((boySelected || girlSelected) && (michaelSelected || gabrielSelected || raphaelSelected || urielSelected)) {
            startButtonReady = true
            lets_start_button.setBackgroundColor(Color.parseColor("#ffbb33"))
            lets_start_button.setTextColor(Color.parseColor("#000000"))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
