package com.rhnlf.storyapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rhnlf.storyapp.data.remote.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKeys::class], version = 2, exportSchema = false
)
abstract class StoryDb : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDb? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, StoryDb::class.java, "story_db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}