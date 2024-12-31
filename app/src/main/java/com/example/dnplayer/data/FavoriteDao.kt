package com.example.dnplayer.data

import androidx.room.*
@Dao
interface FavoriteDao {
    @Query("SELECT songId FROM favorite WHERE isLiked = 1")
    suspend fun getLikedSongIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorite WHERE isLiked = 1")
    suspend fun getAllLikedSongs(): List<Favorite>

    // Replace this with the new implementation
    suspend fun isSongLiked(songId: Int): Boolean {
        val likedSongIds = getLikedSongIds()
        return likedSongIds.contains(songId)
    }

    @Query("UPDATE favorite SET isLiked = :isLiked WHERE songId = :songId")
    suspend fun updateFavorite(songId: Int, isLiked: Boolean)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)
}

