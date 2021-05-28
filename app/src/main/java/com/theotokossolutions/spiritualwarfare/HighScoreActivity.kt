package com.theotokossolutions.spiritualwarfare

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HighScoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, OptionsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
