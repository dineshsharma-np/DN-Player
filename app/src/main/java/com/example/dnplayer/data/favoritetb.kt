package com.example.dnplayer.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val songId: Int,
    val isLiked: Boolean
)
