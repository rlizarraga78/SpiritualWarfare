package com.theotokossolutions.spiritualwarfare

//import android.R
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.*
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class GameActivity : AppCompatActivity() {
    private val screenY = Resources.getSystem().displayMetrics.heightPixels.toFloat()
    private val screenX = Resources.getSystem().displayMetrics.widthPixels.toFloat()
    private val screenCenterX = (screenX / 2)
    private val screenCenterY = (screenY / 2)

    private val humanWidth = screenCenterX / 7f
    private val humanHeight = screenCenterX / 5f
    private var humanSpeed = screenY / 333

    private val angelWidth = screenCenterX / 4f
    private val angelHeight = screenCenterX / 2f

    private val demonWidth = screenCenterX / 6f
    private val demonHeight = screenCenterX / 6f

    private val temptationWidth = screenCenterX / 5f
    private val temptationHeight = screenCenterX / 5f
    private var temptationSpeed = screenY / 250

    private val explosionWidth = screenCenterX / 5f
    private val explosionHeight = screenCenterX / 5f

    private val shieldWidth = screenCenterX / 10f
    private var shieldLength = 100.0f
    private var shieldLengthLimit = 100.0f
    private var shieldRecoveryFast = true

    private var attackTime : Int = 0
    private var attackTimer = 0
    private var fastAttack = 3
    private var slowAttack = 7

    private var isDragging = false
    private var lastX = 0f
    private var lastY = 0f
    private val levelOneDemonLimit = 10
    private val levelTwelveDemonLimit = 40
    private var demonCount = 0

    private var level : Int = 0
    private var human : Int = 0
    private var angel : Int = 0

    private var humanCostumeNumber = 1
    private var humanDirection = 0    //0 = up, 1 = right, 2 = left, 3 = down
    private lateinit var humanImage : ImageView

    private lateinit var temptationImage : ImageView
    private var temptationLocations = Vector<Float>()
    private var temptationNumber = 0
    private var temptationPresent = false
    private var temptationRight = true
    private var temptationHealth = 4
    private var temptationHealthMax = 4

    private val handler = Handler()
    private lateinit var runnable: Runnable

    private var gameover = false
    private var playerWin = false
    private var demonLock = false

    class ShieldBit {
        lateinit var image : ImageView
        var stage = 0
    }
    private var shieldList = Vector<ShieldBit>()

    class Demon {
        lateinit var image : ImageView
        var up = false
        var right = true
        var health = 3
        var shakeRight = true
        var shakeUp = true
        var touchingShield = false
    }
    private var demonFrequency = 0
    private var demonFrequencyCount = 0
    private var demonList = Vector<Demon>()
    private var demonStartTop = true
    private var demonSpeed = screenY / 100

    private var pause = false
    private var popupDisplayed = false

    private lateinit var bgImage : AnimationDrawable
    private var stopMusic = true
    private var screenFlash = false

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR)

        //GET USER/LEVEL INFO FROM SHARED PREFERENCES
        val prefs = this.getSharedPreferences("DATA", 0)
        var editor = prefs.edit()

        level = prefs.getInt("level", -1)
        human = prefs.getInt("human", -1)
        angel = prefs.getInt("angel", -1)

        Log.e("LEVEL: ", ""+level)

        //MAKE SURE ALL VALUES ARE LOADED
        if (human == -1 || angel == -1 || level == -1) {
            Toast.makeText(this,  "Error loading level" , Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //INITIALIZE ATTACK AND SHIELD INFO
        when(angel){
            1 -> {
                attackTime = fastAttack
                shieldRecoveryFast = true
            }
            2 -> {
                attackTime = fastAttack
                shieldRecoveryFast = false
            }
            3 -> {
                attackTime = slowAttack
                shieldRecoveryFast = true
            }
            4 -> {
                attackTime = slowAttack
                shieldRecoveryFast = false
            }
        }

        //SET DEMON FREQUENCY
        when(level){
            1 -> demonFrequency = 70
            2 -> demonFrequency = 60
            3 -> demonFrequency = 51
            4 -> demonFrequency = 43
            5 -> demonFrequency = 36
            6 -> demonFrequency = 30
            7 -> demonFrequency = 25
            8 -> demonFrequency = 21
            9 -> demonFrequency = 18
            10 -> demonFrequency = 16
            11 -> demonFrequency = 15
            12 -> demonFrequency = 12
        }

        //SET LEVEL LAYOUT
        when (level){
            1 -> {
                game_background.setImageResource(R.drawable.level_1)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.guardian_angel_music)
            }
            2 -> {
                game_background.setImageResource(R.drawable.level_2)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.annunciation_music)
            }
            3 -> {
                game_background.setImageResource(R.drawable.level_3)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.nativity_music)
            }
            4 -> {
                game_background.setImageResource(R.drawable.level_4)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.agni_parthene_music)
            }
            5 -> {
                game_background.setImageResource(R.drawable.level_5)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.theophany_troparion_music)
            }
            6 -> {
                game_background.setImageResource(R.drawable.level_6)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.psalm_135_music)
            }
            7 -> {
                game_background.setImageResource(R.drawable.level_7)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.kyrie_eleison_music)
            }
            8 -> {
                game_background.setImageResource(R.drawable.level_8)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.christos_anesti_music)
            }
            9 -> {
                game_background.setImageResource(R.drawable.level_9)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.cherubic_hymn_music)
            }
            10 -> {
                game_background.setImageResource(R.drawable.level_10)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.ladder_to_heaven_music)
            }
            11 -> {
                game_background.setImageResource(R.drawable.level_11)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.the_day_of_judgement_music)
            }
            12 -> {
                game_background.setImageResource(R.drawable.level_12)
                if(!AudioPlayer.isPlaying) AudioPlayer.changeSong(this, R.raw.the_high_priest_music)
            }
            else -> {
                Toast.makeText(this,  "Error loading level" , Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        //TEMPTATION SETUP
        temptationImage = ImageView(this)

        setTemptationImage()
        val layoutParamsTemptation = ConstraintLayout.LayoutParams(temptationWidth.toInt(), temptationHeight.toInt())
        temptationImage.x = screenX + 100
        temptationImage.y = screenCenterY
        game_layout.addView(temptationImage, layoutParamsTemptation)

        val temptationDistance = screenY / level
        var tempLoc = screenY - (temptationHeight / 2)
        for(i in 1 until level){
            tempLoc -= temptationDistance
            if(tempLoc >= screenY - temptationHeight*3) {
                var tempLoc2 = screenY - temptationHeight*3
                temptationLocations.add(tempLoc2)
            }
            else{
                temptationLocations.add(tempLoc)
            }
        }

        //HUMAN SETUP
        humanImage = ImageView(this)
        //setting image resource
        if(human == 1) {
            humanImage.setBackgroundResource(R.drawable.boy_up_animation)
            bgImage = humanImage.background as AnimationDrawable
            bgImage.start()
        } else if (human == 2){
            humanImage.setBackgroundResource(R.drawable.girl_up_animation)
            bgImage = humanImage.background as AnimationDrawable
            bgImage.start()
        }
        val layoutParamsHuman = ConstraintLayout.LayoutParams(humanWidth.toInt(), humanHeight.toInt())

        // Setting position of our ImageView
        humanImage.x = screenCenterX - (humanWidth / 2)
        humanImage.y = screenY - humanHeight - 100

        // Finally Adding the imageView to RelativeLayout and its position
        game_layout.addView(humanImage, layoutParamsHuman)


        //ANGEL SETUP
        val angelImage = ImageView(this)
        when(angel) {
            1 -> angelImage.setBackgroundResource(R.drawable.michael)
            2 -> angelImage.setBackgroundResource(R.drawable.gabriel)
            3 -> angelImage.setBackgroundResource(R.drawable.raphael)
            4 -> angelImage.setBackgroundResource(R.drawable.uriel)
        }
        val layoutParamsAngel = ConstraintLayout.LayoutParams(angelWidth.toInt(), angelHeight.toInt())
        angelImage.x = screenCenterX - (humanWidth / 2)
        angelImage.y = screenCenterY - (screenCenterY / 2)
        game_layout.addView(angelImage, layoutParamsAngel)

        //EXPLOSION SETUP
        val explosionImage = ImageView(this)
        explosionImage.setImageResource(R.drawable.explosion)
        val layoutParamsExplosion = ConstraintLayout.LayoutParams(explosionWidth.toInt(), explosionHeight.toInt())
        explosionImage.x = screenX + 100
        explosionImage.y = screenY
        explosionImage.bringToFront()
        game_layout.addView(explosionImage, layoutParamsExplosion)

        //Bomb Clicker
        bomb_image.setOnClickListener{
            if(progressBar.progress >= 99){
                //vibrate(true)
                screenFlash = true
                for (dmn in demonList) dmn.image.setImageResource(android.R.color.transparent)
                demonList.clear()
                shieldLength = 0f
                screenFlash = true
            }
        }

        //SET CLICKER FOR ANGEL
        game_layout.setOnTouchListener { v: View, event: MotionEvent ->
            val x = event.x
            val y = event.y

            when(event.action){
                //PRESS DOWN
                MotionEvent.ACTION_DOWN -> {
                    angelImage.x = x - (angelWidth / 2)
                    angelImage.y = y - (angelHeight / 2)

                    lastX = angelImage.x
                    lastY = angelImage.y

                    if(attackTimer > attackTime) {
                        //Hit temptation
                        if (kotlin.math.abs((temptationImage.x + (temptationWidth / 2)) - (angelImage.x + (angelWidth / 2))) < angelWidth / 2
                            && kotlin.math.abs((temptationImage.y + (temptationHeight / 2)) - (angelImage.y + (angelHeight / 2))) < angelHeight / 2
                        ) {
                            //vibrate(false)

                            explosionImage.x = x - (explosionWidth / 2)
                            explosionImage.y = y - (explosionHeight / 2)
                            explosionImage.bringToFront()
                            attackTimer = 0

                            if (temptationHealth > 1) temptationHealth--
                            else {
                                temptationImage.x = screenX + 100
                                temptationNumber++
                                temptationPresent = false
                                temptationRight = !temptationRight

                                setTemptationImage()
                            }
                        }

                        //Hit demons
                        var i = 0
                        while(true) {
                            if(demonLock)
                                continue
                            demonLock = true

                            for (dmn in demonList) {
                                if (kotlin.math.abs((dmn.image.x + (demonWidth / 2)) - (angelImage.x + (angelWidth / 2))) < angelWidth / 2
                                    && kotlin.math.abs((dmn.image.y + (demonHeight / 2)) - (angelImage.y + (angelHeight / 2))) < angelHeight / 2
                                ) {
                                    //vibrate(false)

                                    explosionImage.x = x - (explosionWidth / 2)
                                    explosionImage.y = y - (explosionHeight / 2)
                                    explosionImage.bringToFront()
                                    attackTimer = 0

                                    if (dmn.health > 1) {
                                        dmn.health--
                                        break
                                    }
                                    else {
                                        dmn.image.setImageResource(android.R.color.transparent)
                                        demonList.removeAt(i)
                                        break
                                    }
                                } else (i++)
                            }

                            demonLock = false
                            break
                        }
                    }
                }
                //DRAG
                MotionEvent.ACTION_MOVE -> {
                    isDragging = true
                    angelImage.x = x - (angelWidth / 2)
                    angelImage.y = y - (angelHeight / 2)

                    val tempX = lastX - angelImage.x
                    val tempY = lastY - angelImage.y

                    val distance = sqrt((tempX * tempX) + (tempY * tempY))
                    val iter = (distance / (shieldWidth/2)).toInt()
                   // val iter = distance.toInt()

                    val sliceX = tempX / iter
                    val sliceY = tempY / iter


                    if (iter >= 1) {
                        for (i in 0..iter) {
                            if (shieldLength > 0) {
                                //Set shield image
                                val bit = ShieldBit()
                                bit.stage = 1
                                val layoutParamsShield =
                                    ConstraintLayout.LayoutParams(
                                        shieldWidth.toInt(),
                                        shieldWidth.toInt()
                                    )
                                bit.image = ImageView(this@GameActivity)
                                bit.image.setImageResource(R.drawable.shield_1)
                                game_layout.addView(bit.image, layoutParamsShield)
                                bit.image.x = (x - (angelWidth / 2)) + (sliceX * (i + 1))
                                bit.image.y = (y - (angelWidth / 2)) + (sliceY * (i + 1))
                                shieldList.add(bit)
                                shieldLength -= 1
                            }
                        }
                    }
                    lastX = angelImage.x
                    lastY = angelImage.y
                }
                //LIFT UP
                MotionEvent.ACTION_UP -> {
                    isDragging = false
                }
            }

            true
        }

        val handler = Handler()
        handler.postDelayed(Runnable { // Do something after 5s = 5000ms

            //START AUDIO
            if(!AudioPlayer.isPlaying) {
                AudioPlayer.resumeAudio()
            }
        }, 150)

        //CREATE RUNNABLE
        runnable = Runnable {
            if(!pause) {
                if (!gameover && !playerWin) {
                    runOnUiThread {

                        //Shield Bar
                        if(humanImage.y > progressContainer.y - humanImage.height){
                            progressContainer.alpha = 0.4f
                        }
                        else progressContainer.alpha = 1.0f

                        if (shieldLength <= shieldLengthLimit) progressBar.progress = shieldLength.toInt()
                        if (shieldLength > shieldLengthLimit) shieldLength = shieldLengthLimit

                        //Activate bomb
                        if (shieldLength >= 99f) bomb_image.alpha = 1.0f
                        else bomb_image.alpha = 0.4f

                        //Attack Timer
                        attackTimer++
                        if (attackTimer == 3) {
                            explosionImage.x = screenX + 100
                            explosionImage.y = screenY
                            explosionImage.bringToFront()
                        }

                        //Bomb Flash
                        if(game_layout.alpha < 1.0f){
                            game_layout.alpha += 0.1f
                        }

                        if(screenFlash){
                            game_layout.alpha = 0.0f
                            screenFlash = false
                        }

                        //HUMAN MOVEMENT
                        val originalDirection = humanDirection;
                        humanDirection = 0

                        if (humanImage.x + humanWidth / 2 < screenCenterX) humanDirection = 1
                        else if (humanImage.x + humanWidth / 2 > screenCenterX) humanDirection = 2

                        if (level in 2..12) {
                            //Check for temptation
                            if (temptationNumber < temptationLocations.size) {
                                if (humanImage.y <= temptationLocations.elementAt(temptationNumber)) {
                                    //No temptation present
                                    if (!temptationPresent) {
                                        temptationImage.y =
                                            temptationLocations.elementAt(temptationNumber)
                                        if (temptationRight) temptationImage.x = screenX
                                        else temptationImage.x = 0 - temptationWidth
                                        temptationPresent = true
                                        temptationHealth = temptationHealthMax
                                    }

                                    //Release temptation
                                    if (temptationRight) {
                                        humanDirection = 1
                                        if (temptationImage.x > screenX - temptationWidth) temptationImage.x -= temptationSpeed
                                    } else {
                                        if (temptationImage.x < 0) temptationImage.x += temptationSpeed
                                        humanDirection = 2
                                    }
                                }
                                //Withdraw temptation
                                else {
                                    if (temptationPresent) {
                                        if (temptationRight) {
                                            if (temptationImage.x < screenX) temptationImage.x += temptationSpeed
                                        } else {
                                            if (temptationImage.x > 0 - temptationWidth) temptationImage.x -= temptationSpeed
                                        }
                                    }
                                }
                            }
                        }

                        //Check if grabbed by demon
                        while(true) {
                            if(demonLock)
                                continue
                            demonLock = true

                            for (dmn in demonList) {
                                if (kotlin.math.abs((dmn.image.x + (demonWidth / 2)) - (humanImage.x + (humanWidth / 2))) < humanWidth / 2
                                    && kotlin.math.abs((dmn.image.y + (demonHeight / 2)) - (humanImage.y + (humanHeight / 2))) < humanHeight / 2
                                    && !dmn.touchingShield
                                ) {
                                    humanDirection = 3
                                    break
                                }
                            }
                            demonLock = false
                            break
                        }

                        //Move human in correct direction
                        when (humanDirection) {
                            0 -> humanImage.y -= humanSpeed
                            1 -> humanImage.x += humanSpeed
                            2 -> humanImage.x -= humanSpeed
                            3 -> humanImage.y += humanSpeed
                        }

                        //Switch Costume
                        if (originalDirection != humanDirection) {
                            if (human == 1) {
                                when (humanDirection) {
                                    0 -> {
                                        humanImage.setBackgroundResource(R.drawable.boy_up_animation)
                                        val bgImage = humanImage.background as AnimationDrawable
                                        bgImage.start()
                                    }
                                    1 -> {
                                        humanImage.setBackgroundResource(R.drawable.boy_right_animation)
                                        val bgImage = humanImage.background as AnimationDrawable
                                        bgImage.start()
                                    }
                                    2 -> {
                                        humanImage.setBackgroundResource(R.drawable.boy_left_animation)
                                        val bgImage = humanImage.background as AnimationDrawable
                                        bgImage.start()
                                    }
                                }
                            } else if (human == 2) {
                                when (humanDirection) {
                                    0 -> {
                                        humanImage.setBackgroundResource(R.drawable.girl_up_animation)
                                        val bgImage = humanImage.background as AnimationDrawable
                                        bgImage.start()
                                    }
                                    1 -> {
                                        humanImage.setBackgroundResource(R.drawable.girl_right_animation)
                                        val bgImage = humanImage.background as AnimationDrawable
                                        bgImage.start()
                                    }
                                    2 -> {
                                        humanImage.setBackgroundResource(R.drawable.girl_left_animation)
                                        val bgImage = humanImage.background as AnimationDrawable
                                        bgImage.start()
                                    }
                                }
                            }
                        }

                    }

                    //Demon Clones
                    demonFrequencyCount++
                    if (demonFrequencyCount >= demonFrequency) {
                        val dem = Demon()
                        //Set demon image
                        val layoutParamsDemon =
                            ConstraintLayout.LayoutParams(demonWidth.toInt(), demonHeight.toInt())
                        dem.image = ImageView(this@GameActivity)
                        dem.image.setImageResource(R.drawable.demon_right_1)
                        game_layout.addView(dem.image, layoutParamsDemon)

                        //Start demon at top
                        dem.image.x = (0..screenX.toInt()).random().toFloat()
                        dem.image.y = 0 - dem.image.height.toFloat()

                        if (dem.image.x < screenCenterX) dem.image.setImageResource(R.drawable.demon_right_1)
                        else dem.image.setImageResource(R.drawable.demon_left_1)

                        //Add demon to list
                        demonList.add(dem)
                        demonFrequencyCount = 0
                        demonCount++
                    }

                    //Set Direction of Demons
                    while(true) {

                        if(demonLock)
                            continue
                        demonLock = true

                        for (dmn in demonList) {

                            //Set direction
                            val tempX = humanImage.x - dmn.image.x
                            val tempY = humanImage.y - dmn.image.y

                            val radians = atan(tempY / tempX)
                            val demonX = cos(radians)
                            val demonY = sin(radians)

                            //Check if touching shield
                            val iterator = shieldList.iterator()
                            var isMove = true
                            while (iterator.hasNext()) {
                                val bit = iterator.next()
                                if (kotlin.math.abs((dmn.image.x + (demonWidth / 2)) - bit.image.x) < demonWidth / 4
                                    && kotlin.math.abs((dmn.image.y + (demonHeight / 2)) - bit.image.y) < demonHeight / 4
                                ) {
                                    isMove = false
                                    dmn.touchingShield = true
                                }
                            }

                            //Move demon
                            if (isMove) {
                                dmn.touchingShield = false

                                if (humanImage.x < dmn.image.x) {
                                    dmn.image.x = dmn.image.x - demonSpeed * demonX
                                    dmn.image.y = dmn.image.y - demonSpeed * demonY
                                } else {
                                    dmn.image.x = dmn.image.x + demonSpeed * demonX
                                    dmn.image.y = dmn.image.y + demonSpeed * demonY
                                }

                            } else {

                                //Shake demon if caught in shield
                                if (dmn.shakeRight) {
                                    dmn.image.x += demonSpeed / 2
                                    dmn.shakeRight = false
                                } else {
                                    dmn.image.x -= demonSpeed / 2
                                    dmn.shakeRight = true
                                }

                                if (dmn.shakeUp) {
                                    dmn.image.y -= demonSpeed / 2
                                    dmn.shakeUp = false
                                } else {
                                    dmn.image.y += demonSpeed / 2
                                    dmn.shakeUp = true
                                }
                            }
                        }
                        demonLock = false
                        break
                    }

                    //Update Shield
                    val iterator = shieldList.iterator()
                    while (iterator.hasNext()) {
                        val bit = iterator.next()
                        bit.stage++
                        when (bit.stage) {
                            2 -> bit.image.setImageResource(R.drawable.shield_1)
                            4 -> bit.image.setImageResource(R.drawable.shield_2)
                            6 -> bit.image.setImageResource(R.drawable.shield_3)
                            8 -> bit.image.setImageResource(R.drawable.shield_4)
                            10 -> bit.image.setImageResource(R.drawable.shield_5)
                            12 -> bit.image.setImageResource(R.drawable.shield_6)
                            14 -> bit.image.setImageResource(R.drawable.shield_7)
                            16 -> bit.image.setImageResource(R.drawable.shield_8)
                            18 -> bit.image.setImageResource(R.drawable.shield_9)
                            20 -> bit.image.setImageResource(R.drawable.shield_10)
                            22 -> bit.image.setImageResource(R.drawable.shield_11)
                            24 -> bit.image.setImageResource(R.drawable.shield_10)
                            26 -> bit.image.setImageResource(R.drawable.shield_9)
                            28 -> bit.image.setImageResource(R.drawable.shield_8)
                            30 -> bit.image.setImageResource(R.drawable.shield_7)
                            32 -> bit.image.setImageResource(R.drawable.shield_6)
                            34 -> bit.image.setImageResource(R.drawable.shield_5)
                            36 -> bit.image.setImageResource(R.drawable.shield_4)
                            38 -> bit.image.setImageResource(R.drawable.shield_3)
                            40 -> bit.image.setImageResource(R.drawable.shield_2)
                            42 -> bit.image.setImageResource(R.drawable.shield_1)
                            !in 1..42 -> {
                                bit.image.setImageResource(android.R.color.transparent)
                                iterator.remove()
                            }
                        }
                    }

                    //Recharge shield
                    if (!isDragging && shieldLength < shieldLengthLimit) {
                        if (shieldRecoveryFast) shieldLength += 0.7f
                        else shieldLength += 0.3f
                    }

                    //Check if player wins or loses
                    if (humanImage.y <= 0)
                        playerWin = true
                    else if (humanImage.y >= screenY || humanImage.x >= screenX - temptationWidth - humanWidth
                        || humanImage.x <= temptationWidth
                    ) gameover = true

                    handler.postDelayed(runnable, 70)
                } else if (gameover) {
                    //Change Background tint
                    //ObjectAnimator.ofFloat(game_layout.background, "alpha", 0f).setDuration(10000).start()
                    //tempImage.setImageDrawable(game_layout.background)
                    //tempImage.visibility = 0
                    //ObjectAnimator.ofFloat(tempImage, "alpha", 0f).setDuration(10000).start();

                    //val anim: Animation =
                    //  AnimationUtils.loadAnimation(this@GameActivity, R.anim.fade_in)
                    //imageView.setAnimation(anim)
                    //anim.start()
                    ObjectAnimator.ofFloat(game_layout.background, "alpha", 0f).setDuration(5000)
                        .start()


                    //POPUP
                    // Initialize a new layout inflater instance
                    val inflater: LayoutInflater =
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                    // Inflate a custom view using layout inflater
                    val view = inflater.inflate(R.layout.activity_gameover_pop_up, null)

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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

                    // Set click listener for popup window's text view
                    yesButton.setOnClickListener {
                        stopMusic = false
                        AudioPlayer.isPlaying = true
                        popupWindow.dismiss()
                        val intent = Intent(this, GameActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    // Set a click listener for popup's button widget
                    noButton.setOnClickListener {
                        stopMusic = true
                        AudioPlayer.isPlaying = false
                        popupWindow.dismiss()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    // Finally, show the popup window on app
                    TransitionManager.beginDelayedTransition(game_layout)
                    popupWindow.showAtLocation(
                        game_layout, // Location to display popup window
                        Gravity.CENTER, // Exact position of layout to display popup
                        0, // X offset
                        0 // Y offset
                    )
                } else if (playerWin) {
                    editor.putInt("level", level + 1)
                    editor.apply()

                    stopMusic = false

                    val intent = Intent(this, VideoActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            else handler.postDelayed(runnable, 300)
        }

        //START GAME LOOP
        handler.postDelayed(runnable, 70)
    }

    private fun setTemptationImage(){
        val rand = (1..12).random()
        when (rand) {
            1 -> temptationImage.setImageResource(R.drawable.temptation_1)
            2 -> temptationImage.setImageResource(R.drawable.temptation_2)
            3 -> temptationImage.setImageResource(R.drawable.temptation_3)
            4 -> temptationImage.setImageResource(R.drawable.temptation_4)
            5 -> temptationImage.setImageResource(R.drawable.temptation_5)
            6 -> temptationImage.setImageResource(R.drawable.temptation_6)
            7 -> temptationImage.setImageResource(R.drawable.temptation_7)
            8 -> temptationImage.setImageResource(R.drawable.temptation_8)
            9 -> temptationImage.setImageResource(R.drawable.temptation_9)
            10 -> temptationImage.setImageResource(R.drawable.temptation_10)
            11 -> temptationImage.setImageResource(R.drawable.temptation_11)
            12 -> temptationImage.setImageResource(R.drawable.temptation_12)
        }
    }

    private fun vibrate(longVibrate : Boolean){
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(longVibrate) vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
            else vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            if(longVibrate) vibrator.vibrate(1000)
            else vibrator.vibrate(200)
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onBackPressed() {

        if(!popupDisplayed){
            popupDisplayed = true
            pause = true
            bgImage.stop()

            ObjectAnimator.ofFloat(game_layout.background, "alpha", 0f).setDuration(5000)
                .start()

            //POPUP
            // Initialize a new layout inflater instance
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.activity_stop_game_pop_up, null)

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

            // Set click listener for popup window's text view
            yesButton.setOnClickListener {
                //AudioPlayer.pauseAudio()
                stopMusic = true
                AudioPlayer.isPlaying = false
                popupWindow.dismiss()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Set a click listener for popup's button widget
            noButton.setOnClickListener {
                popupDisplayed = false
                pause = false
                bgImage.start()
                popupWindow.dismiss()
            }

            // Finally, show the popup window on app
            TransitionManager.beginDelayedTransition(game_layout)
            popupWindow.showAtLocation(
                game_layout, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )
        }

    }

    override fun onStop() {
        super.onStop()
        pause = true
        if(stopMusic) AudioPlayer.pauseAudio()
    }

    override fun onResume() {
        super.onResume()
        if(!popupDisplayed) pause = false
    }

    override fun onDestroy() {
        //if(stopMusic) AudioPlayer.pauseAudio()
        // stop Handler
        handler.removeCallbacks(runnable)
        // to stop anonymous runnable use aHandler.removeCallbacksAndMessages(null);
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
        AudioPlayer.resumeAudio()
    }

}
