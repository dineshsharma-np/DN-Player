package com.example.dnplayer

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val path: String,
    val duration: Long,
    var isLiked: Boolean = false
)
