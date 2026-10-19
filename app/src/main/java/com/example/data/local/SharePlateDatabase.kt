package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.converters.SharePlateTypeConverters
import com.example.data.local.dao.HistoricalRescueDao
import com.example.data.local.dao.OfflineDraftDao
import com.example.data.local.entity.HistoricalRescueEntity
import com.example.data.local.entity.OfflineDraftEntity

/**
 * Local Room Database for SharePlate application.
 * Manages offline donation drafts and historical food rescue audit records
 * with thread-safe singleton initialization.
 */
@Database(
    entities = [
        OfflineDraftEntity::class,
        HistoricalRescueEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(SharePlateTypeConverters::class)
abstract class SharePlateDatabase : RoomDatabase() {

    abstract fun offlineDraftDao(): OfflineDraftDao
    abstract fun historicalRescueDao(): HistoricalRescueDao

    companion object {
        @Volatile
        private var INSTANCE: SharePlateDatabase? = null

        fun getInstance(context: Context): SharePlateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SharePlateDatabase::class.java,
                    "shareplate_local.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
