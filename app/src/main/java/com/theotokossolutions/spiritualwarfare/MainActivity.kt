package com.theotokossolutions.spiritualwarfare

import AudioPlayer
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var music : MediaPlayer
    private var stopMusic = true
    var startMusic = true

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //SET ORIENTATION
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        //SET PLAYER
        if(!AudioPlayer.hasPlayer()) {
            AudioPlayer.setPlayer(this, R.raw.intro_theme_music)
            //AudioPlayer.playAudio();
        }

        val handler = Handler()
        handler.postDelayed(Runnable {

            //START AUDIO
            if(!AudioPlayer.isPlaying || AudioPlayer.isStopped) {
                AudioPlayer.changeSong(this, R.raw.intro_theme_music)
            }
        }, 1000)





/**
        //SET MUSIC MEDIAPLAYER
        val playMusic = intent.getStringExtra("MUSIC")
        if(playMusic != null) startMusic = false                          //NEED TO PASS MEDIAPLAYER WITH INTENT FUNCTION TO OPTIONS PAGE

        //START MUSIC
        if(startMusic){
            music = MediaPlayer.create(this@MainActivity, R.raw.uhraybeeah)
            music.isLooping = true
            music.start()
        }
*/
        //NEW GAME BUTTON
        new_game_button.setOnClickListener{

            // Initialize a new layout inflater instance
            val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.activity_pop_up,null)

            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            // Set an elevation for the popup window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 10.0F
            }


            // If API level 23 or higher then execute the code
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.TOP
                popupWindow.enterTransition = slideIn

                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.RIGHT
                popupWindow.exitTransition = slideOut

            }

            // Get the widgets reference from custom view
            val yesButton = view.findViewById<TextView>(R.id.yes_button)
            val noButton = view.findViewById<TextView>(R.id.no_button)
            val outsidePopup = view.findViewById<ImageView>(R.id.pop_up_background)

            // Set click listener for popup window's text view
            yesButton.setOnClickListener{
                stopMusic = false
                popupWindow.dismiss()
                val intent = Intent(this, CharacterScreenActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Set a click listener for popup's button widget
            noButton.setOnClickListener{
                popupWindow.dismiss()
            }

            // Set a click listener for popup's background
            outsidePopup.setOnClickListener{
                popupWindow.dismiss()
            }

            // Set a dismiss listener for popup window
            popupWindow.setOnDismissListener {
                //Toast.makeText(applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
            }

            // Finally, show the popup window on app
            TransitionManager.beginDelayedTransition(root_layout)
            popupWindow.showAtLocation(
                root_layout, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )
        }

        //LOAD GAME BUTTON
        load_game_button.setOnClickListener{
            //Check if previously saved game
            val prefs = this.getSharedPreferences("DATA", 0)
            val level = prefs.getInt("level", -1)

            if(level in 1..13) {
                AudioPlayer.pauseAudio()
                stopMusic = false
                AudioPlayer.isStopped = true;
                val intent = Intent(this, VideoActivity::class.java)
                startActivity(intent)
                finish()
            }
            else Toast.makeText(applicationContext,"Saved game not found",Toast.LENGTH_SHORT).show()
        }

        //OPTIONS BUTTON
        options_button.setOnClickListener{
            stopMusic = false
            val intent = Intent(this, OptionsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true);
    }

    override fun onStop() {
        super.onStop()
        if(stopMusic) AudioPlayer.pauseAudio()
    }

    override fun onRestart() {
        super.onRestart()
        if(!AudioPlayer.isPlaying) AudioPlayer.resumeAudio()
    }

    override fun onResume(){
        super.onResume()
        if(!AudioPlayer.isPlaying) AudioPlayer.resumeAudio()
    }

}
