package com.example.dnplayer

import android.app.Application
import android.content.ContentResolver
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dnplayer.data.Favorite
import com.example.dnplayer.data.DatabaseProvider
import kotlinx.coroutines.launch

class SongViewModel(application: Application) : AndroidViewModel(application) {
    private val favoriteDao = DatabaseProvider.getDatabase(application).favoriteDao()

    // Toggle like status for a song
    fun toggleLike(songId: Int, isLiked: Boolean) {
        viewModelScope.launch {
            val favorite = Favorite(songId = songId, isLiked = isLiked)
            favoriteDao.insertOrUpdateFavorite(favorite)
        }
    }

    // Check if the song is liked
    suspend fun isSongLiked(songId: Int): Boolean {
        return favoriteDao.isSongLiked(songId)
    }

    // Get liked songs from the database and MediaStore
    fun getLikedSongs(callback: (List<Song>) -> Unit) {
        viewModelScope.launch {
            val likedSongIds = favoriteDao.getLikedSongIds()
            val allSongs = getSongsFromLocalStorage()

            // Filter songs by matching IDs
            val likedSongs = allSongs.filter { song ->
                likedSongIds.contains(song.id.toInt()) // Convert if necessary
            }

            callback(likedSongs)
        }
    }

    // Fetch all songs from local storage
    private fun getSongsFromLocalStorage(): List<Song> {
        val songs = mutableListOf<Song>()
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION // Add duration for extra info
        )

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null, null, null
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val pathColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)
            val durationColumn = it.getColumnIndex(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val songId = it.getInt(idColumn) // Use Int for song ID
                val title = it.getString(titleColumn) ?: "Unknown Title"
                val artist = it.getString(artistColumn) ?: "Unknown Artist"
                val path = it.getString(pathColumn) ?: "Unknown Path"
                val duration = it.getLong(durationColumn) // Retrieve duration

                // Create Song object and add to list
                val song = Song(id = songId.toString(), title = title, artist = artist, path = path, duration = duration)
                songs.add(song)
            }
        }

        return songs
    }
}
