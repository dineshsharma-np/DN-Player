package com.example.dnplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class FavoriteSongsAdapter(
    private val songs: List<Song>
) : RecyclerView.Adapter<FavoriteSongsAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.song_title)
        val artist: TextView = itemView.findViewById(R.id.song_artist)
        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]

        // Set song title and artist
        holder.title.text = song.title
        holder.artist.text = song.artist

        // Set like button color to red
        holder.likeButton.setColorFilter(
            ContextCompat.getColor(holder.itemView.context, R.color.red)
        )

        // Optionally, handle like button clicks if needed
        holder.likeButton.setOnClickListener {

        }
    }

    override fun getItemCount() = songs.size
}
