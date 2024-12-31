package com.example.dnplayer
import java.io.File

data class Folder(val folderPath: String, val songs: List<Song>) {
    val displayName: String
        get() = File(folderPath).name
}
