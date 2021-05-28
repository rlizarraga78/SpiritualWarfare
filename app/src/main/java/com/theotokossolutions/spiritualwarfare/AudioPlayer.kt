import android.content.Context
import android.media.MediaPlayer
import android.util.Log

object AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
    var isStopped = false

    fun setPlayer(c: Context?, id: Int){
        mediaPlayer = MediaPlayer.create(c, id)
        mediaPlayer!!.isLooping =true
    }

    fun changeSong(c: Context?, id: Int){
        mediaPlayer?.stop()
        mediaPlayer?.release()
        isPlaying = true
        isStopped = false
        mediaPlayer = MediaPlayer.create(c, id)
        mediaPlayer!!.isLooping =true
        mediaPlayer!!.start()
    }

    fun playAudio() {
        if (!mediaPlayer!!.isPlaying) {
            isPlaying = true
            if(isStopped) {
                mediaPlayer!!.prepare()
                isStopped = false
            }
            mediaPlayer!!.start()
        }
    }

    fun resumeAudio() {
        isPlaying = true
        isStopped = false
        mediaPlayer!!.start()
    }

    fun stopAudio() {
        isPlaying = false
        isStopped = true
        mediaPlayer!!.stop()
    }

    fun pauseAudio() {
            isPlaying = false
            isStopped = false
            mediaPlayer!!.pause()
    }

    fun hasPlayer() : Boolean{
        return mediaPlayer != null
    }
}