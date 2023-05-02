package com.rhnlf.storyapp.data.di

import android.content.Context
import com.rhnlf.storyapp.data.local.db.StoryDb
import com.rhnlf.storyapp.data.remote.api.ApiConfig
import com.rhnlf.storyapp.data.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDb.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService, context)
    }
}