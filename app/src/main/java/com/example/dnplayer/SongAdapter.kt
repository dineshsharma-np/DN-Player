package com.example.dnplayer

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(
    private var songList: MutableList<Song>, // Use MutableList here
    private val playButtonClickListener: PlayButtonClickListener, // Listener for play button
    private val likeButtonClickListener: LikeButtonClickListener // Listener for like button
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    interface PlayButtonClickListener {
        fun onPlayButtonClick(song: Song, position: Int) // Callback for play button click
    }

    interface LikeButtonClickListener {
        fun onLikeButtonClick(song: Song, position: Int) // Callback for like button click
    }

    // Track the currently playing song position
    private var currentlyPlayingPosition: Int = -1

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songTitle: TextView = itemView.findViewById(R.id.song_title)
        val songArtist: TextView = itemView.findViewById(R.id.song_artist)
        val playButton: ImageView = itemView.findViewById(R.id.play) // Play button
        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton) // Like button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.songTitle.text = song.title
        holder.songArtist.text = song.artist

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
            intent.putExtra("song_id", song.id) // Pass song ID
            intent.putExtra("song_title", song.title) // Pass song title
            intent.putExtra("song_artist", song.artist) // Pass song artist
            intent.putExtra("song_duration", song.duration)
            intent.putExtra("song_path", song.path)
            holder.itemView.context.startActivity(intent) // Start activity

        }
        // Update the button icon based on playing state
        if (position == currentlyPlayingPosition) {
            holder.playButton.setImageResource(R.drawable.pause) // Set pause icon if song is playing
        } else {
            holder.playButton.setImageResource(R.drawable.play) // Set play icon if song is not playing
        }

        // Change the like button color based on the isLiked status
        if (song.isLiked) {
            holder.likeButton.setColorFilter(Color.RED) // Set color to red when liked
        } else {
            holder.likeButton.clearColorFilter() // Remove color filter when not liked
        }

        // Set play button click listener
        holder.playButton.setOnClickListener {
            val previousPosition = currentlyPlayingPosition
            currentlyPlayingPosition = if (position == currentlyPlayingPosition) {
                -1 // Pause the current song
            } else {
                position // Play the new song
            }

            // Notify the adapter to update the UI for both old and new positions
            notifyItemChanged(previousPosition)
            notifyItemChanged(position)

            playButtonClickListener.onPlayButtonClick(song, position) // Trigger listener
        }

        // Set like button click listener
        holder.likeButton.setOnClickListener {
            song.isLiked = !song.isLiked // Toggle the like status
            likeButtonClickListener.onLikeButtonClick(song, position) // Notify listener
            notifyItemChanged(position) // Refresh the item to update the like button color
        }
    }


    override fun getItemCount(): Int = songList.size

    // Add a method to update the song list from the fragment
    fun updateList(newList: MutableList<Song>) {
        songList = newList
        notifyDataSetChanged() // Notify the adapter to update the data
    }
}
