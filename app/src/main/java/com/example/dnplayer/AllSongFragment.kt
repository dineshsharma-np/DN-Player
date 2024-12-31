package com.example.dnplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dnplayer.databinding.FragmentAllSongBinding
import java.io.File

class AllSongFragment : Fragment(), SongAdapter.PlayButtonClickListener, FolderAdapter.FolderClickListener, SongAdapter.LikeButtonClickListener {

    private var _binding: FragmentAllSongBinding? = null
    private val binding get() = _binding!!
    private lateinit var songAdapter: SongAdapter
    private lateinit var folderAdapter: FolderAdapter
    private val songList = mutableListOf<Song>()
    private val folderSongMap = mutableMapOf<String, MutableList<Song>>()
    private val folderList = mutableListOf<String>()
    private enum class DisplayMode { ALL_SONGS, BY_FOLDER }
    private var currentDisplayMode = DisplayMode.ALL_SONGS
    private lateinit var currentDisplayedList: MutableList<Song>

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingPosition: Int = -1
    private val songViewModel: SongViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllSongBinding.inflate(inflater, container, false)
        val view = binding.root

        currentDisplayedList = mutableListOf()
        binding.songListRecyclerView.layoutManager = LinearLayoutManager(context)
        songAdapter = SongAdapter(currentDisplayedList, this, this)
        binding.songListRecyclerView.adapter = songAdapter

        binding.folderRecyclerView.layoutManager = LinearLayoutManager(context)
        folderAdapter = FolderAdapter(folderList, this)
        binding.folderRecyclerView.adapter = folderAdapter

        loadSongs()
        setupSearchBar()

        showAllSongs()
        binding.folderIcon.setOnClickListener {
            showFolders()
        }
        binding.allSongsIcon.setOnClickListener {
            showAllSongs() // Show all songs when all songs icon is clicked
        }

        return view
    }

    private fun loadSongs() {
        songList.clear()
        folderSongMap.clear()
        folderList.clear()
        val contentResolver = requireActivity().contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION

        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor = contentResolver.query(uri, projection, selection, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val duration = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val song = Song(id, title, artist, path, duration)
                songList.add(song)

                val file = File(path)
                val folderPath = file.parentFile?.absolutePath ?: "Unknown Folder"
                if (!folderSongMap.containsKey(folderPath)) {
                    folderSongMap[folderPath] = mutableListOf()
                }
                folderSongMap[folderPath]?.add(song)
            }
        }

        folderList.addAll(folderSongMap.keys)

        updateDisplayedList()
    }

    private fun showAllSongs() {
        currentDisplayMode = DisplayMode.ALL_SONGS
        currentDisplayedList.clear()
        currentDisplayedList.addAll(songList)
        songAdapter.updateList(currentDisplayedList)

        binding.songListRecyclerView.visibility = View.VISIBLE
        binding.folderRecyclerView.visibility = View.GONE
    }

    private fun showFolders() {
        currentDisplayMode = DisplayMode.BY_FOLDER
        currentDisplayedList.clear()

        folderList.forEach { folderName ->
            folderSongMap[folderName]?.let { songs ->
                currentDisplayedList.addAll(songs)
            }
        }

        songAdapter.updateList(currentDisplayedList)

        binding.songListRecyclerView.visibility = View.GONE
        binding.folderRecyclerView.visibility = View.VISIBLE
    }

    private fun loadFolders() {
        folderList.clear()
        folderList.addAll(folderSongMap.keys)
        folderAdapter.notifyDataSetChanged()
    }

    private fun loadSongsByFolder() {
        currentDisplayedList.clear()
        folderSongMap.values.forEach { songsInFolder ->
            currentDisplayedList.addAll(songsInFolder)
        }
        songAdapter.updateList(currentDisplayedList)
    }

    private fun toggleDisplayMode() {
        currentDisplayMode = when (currentDisplayMode) {
            DisplayMode.ALL_SONGS -> {
                loadSongsByFolder()
                DisplayMode.BY_FOLDER
            }
            DisplayMode.BY_FOLDER -> {
                updateDisplayedList()
                DisplayMode.ALL_SONGS
            }
        }
        songAdapter.notifyDataSetChanged()
    }

    private fun updateDisplayedList() {
        currentDisplayedList.clear()
        when (currentDisplayMode) {
            DisplayMode.ALL_SONGS -> currentDisplayedList.addAll(songList)
            DisplayMode.BY_FOLDER -> loadSongsByFolder()
        }
        songAdapter.updateList(currentDisplayedList)
    }

    private fun setupSearchBar() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterSongs(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterSongs(query: String) {
        val listToFilter = if (currentDisplayMode == DisplayMode.ALL_SONGS) songList else currentDisplayedList
        currentDisplayedList.clear()
        if (query.isEmpty()) {
            updateDisplayedList()
        } else {
            listToFilter.forEach { song ->
                if (song.title.contains(query, ignoreCase = true) ||
                    song.artist.contains(query, ignoreCase = true)) {
                    currentDisplayedList.add(song)
                }
            }
            songAdapter.updateList(currentDisplayedList)
        }
    }

    override fun onFolderClick(folderName: String) {
        currentDisplayedList.clear()
        currentDisplayedList.addAll(folderSongMap[folderName] ?: emptyList())
        songAdapter.updateList(currentDisplayedList)
    }

    private var currentlyPlayingPositionInMillis: Int = 0

    override fun onPlayButtonClick(song: Song, position: Int) {
        try {
            if (mediaPlayer != null && currentlyPlayingPosition == position) {
                if (mediaPlayer!!.isPlaying) {
                    currentlyPlayingPositionInMillis = mediaPlayer!!.currentPosition
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.seekTo(currentlyPlayingPositionInMillis)
                    mediaPlayer?.start()
                }
            } else {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(song.path)
                    setOnPreparedListener {
                        Log.d("MediaPlayer", "Prepared, starting playback")
                        start()
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("MediaPlayer", "Error occurred: what=$what, extra=$extra")
                        true
                    }
                    prepareAsync()
                }

                val previousPosition = currentlyPlayingPosition
                currentlyPlayingPosition = position

                songAdapter.notifyItemChanged(previousPosition)
                songAdapter.notifyItemChanged(currentlyPlayingPosition)
            }
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Exception occurred: ${e.message}")
            e.printStackTrace()
        }
    }

    // Add toggle like functionality
    override fun onLikeButtonClick(song: Song, position: Int) {
        val isLiked = song.isLiked
        song.isLiked = !isLiked  // Immediately update the song's like state

        // Update the database with the new like state
//        songViewModel.toggleLike(song.id, !isLiked)

        songAdapter.notifyItemChanged(position)  // Notify the adapter that the item has changed
    }


    override fun onResume() {
        super.onResume()

        // Fetch liked songs and update their isLiked state
        songViewModel.getLikedSongs { likedSongs ->
            // Create a set of liked song IDs for fast lookup
            val likedSongIds = likedSongs.map { it.id }.toSet()

            // Update the isLiked property of each song in songList
            songList.forEach { song ->
                song.isLiked = likedSongIds.contains(song.id)
            }
            updateDisplayedList()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }


}
