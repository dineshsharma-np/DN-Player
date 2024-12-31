package com.example.dnplayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale


class PlayerActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)
        val songId = intent.getStringExtra("song_id")
        val songTitle = intent.getStringExtra("song_title")
        val songArtist = intent.getStringExtra("song_artist")
        val songDuration = intent.getLongExtra("song_duration", 0L)
        val songPath = intent.getStringExtra("song_path")

        val durationTextView = findViewById<TextView>(R.id.totalDuration)
        durationTextView.text = formatDuration(songDuration) // Convert to readable format
        val titleTextView = findViewById<TextView>(R.id.playerSongName)
        val artistTextView = findViewById<TextView>(R.id.artistName)
        titleTextView.text = songTitle
        artistTextView.text = songArtist



        Handler(Looper.getMainLooper()).postDelayed({
            titleTextView.apply {
                isSelected = true // Necessary for marquee to start
                ellipsize = TextUtils.TruncateAt.MARQUEE
                marqueeRepeatLimit = -1 // -1 for infinite marquee
                isSingleLine = true
            }
        }, 2000) // Delay for 1 second

        overridePendingTransition(R.anim.`in`, R.anim.out)
        UIUtils.setGradientStatusBar(this, R.drawable.gradient_player_activity, R.id.playerActivityRoot)
        val closeButton = findViewById<ImageButton>(R.id.minimize)

        closeButton?.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.`in`, R.anim.out)
        }


    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.`in`, R.anim.out) // Apply fade animations
    }
    private fun formatDuration(durationInMillis: Long): String {
        val hours = durationInMillis / 3600000 // 3600000 ms = 1 hour
        val minutes = (durationInMillis % 3600000) / 60000 // 60000 ms = 1 minute
        val seconds = (durationInMillis % 60000) / 1000 // 1000 ms = 1 second

        return if (hours > 0) {
            String.format(Locale.US,"%02d:%02d:%02d", hours, minutes, seconds) // Format with hours, minutes, and seconds
        } else {
            String.format(Locale.US,"%02d:%02d", minutes, seconds) // Format with just minutes and seconds
        }
    }
}