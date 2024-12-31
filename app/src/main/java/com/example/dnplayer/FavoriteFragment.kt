package com.example.dnplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dnplayer.data.DatabaseProvider
import com.example.dnplayer.data.FavoriteDao
import com.example.dnplayer.data.Favorite
import com.example.dnplayer.SongViewModel

import com.example.dnplayer.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class FavoriteFragment : Fragment(), SongAdapter.PlayButtonClickListener, SongAdapter.LikeButtonClickListener {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteSongsAdapter: SongAdapter
    private val likedSongsList = mutableListOf<Song>()
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var songViewModel: SongViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        songViewModel = ViewModelProvider(requireActivity())[SongViewModel::class.java]

        favoriteDao = DatabaseProvider.getDatabase(requireContext()).favoriteDao()
        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(context)
        favoriteSongsAdapter = SongAdapter(likedSongsList, this, this) // Pass this for both listeners
        binding.favoriteRecyclerView.adapter = favoriteSongsAdapter

        loadLikedSongs()
        return binding.root
    }

    private fun loadLikedSongs() {
        lifecycleScope.launch(Dispatchers.IO) {
            val likedSongIds = favoriteDao.getLikedSongIds()
            val allSongs = loadSongsFromDevice()
            val likedSongs = allSongs.filter { likedSongIds.contains(it.id.toInt()) }

            withContext(Dispatchers.Main) {
                likedSongsList.clear()
                likedSongsList.addAll(likedSongs)
                favoriteSongsAdapter.updateList(likedSongsList)
            }
        }
    }

    private fun loadSongsFromDevice(): List<Song> {
        val songList = mutableListOf<Song>()
        val contentResolver = requireActivity().contentResolver
        val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            android.provider.MediaStore.Audio.Media._ID,
            android.provider.MediaStore.Audio.Media.TITLE,
            android.provider.MediaStore.Audio.Media.ARTIST,
            android.provider.MediaStore.Audio.Media.DATA,
            android.provider.MediaStore.Audio.Media.DURATION

        )
        val selection = "${android.provider.MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor = contentResolver.query(uri, projection, selection, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media._ID))
                val title = it.getString(it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.TITLE))
                val artist = it.getString(it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.ARTIST))
                val path = it.getString(it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA))
                val duration = it.getLong(it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DURATION))
                val song = Song(id, title, artist, path,  duration)
                songList.add(song)
            }
        }
        return songList
    }

    override fun onPlayButtonClick(song: Song, position: Int) {
        // Handle play button click
        // Example: Show a toast or play the song
    }

    override fun onLikeButtonClick(song: Song, position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val newLikeState = !song.isLiked // Toggle the liked state
            favoriteDao.insertOrUpdateFavorite(Favorite(songId = song.id.toInt(), isLiked = newLikeState))


            withContext(Dispatchers.Main) {
                song.isLiked = newLikeState
                if (!newLikeState) {
                    // Remove the unliked song from the list and update the adapter
                    likedSongsList.removeAt(position)
                    favoriteSongsAdapter.notifyItemRemoved(position)

                    // Show empty state if no liked songs
                    if (likedSongsList.isEmpty()) {
                        binding.emptyStateView.visibility = View.VISIBLE
                    }
                } else {
                    favoriteSongsAdapter.notifyItemChanged(position) // Update the item
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
