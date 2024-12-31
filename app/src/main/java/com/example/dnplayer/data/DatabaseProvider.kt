package com.example.dnplayer.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var database: AppDatabase? = null

    // Returns the instance of the database
    fun getDatabase(context: Context): AppDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "dn_player_database"  // Name of your database
            ).fallbackToDestructiveMigration()  // Optional: Use migrations in production
                .build()
        }
        return database!!
    }
}
