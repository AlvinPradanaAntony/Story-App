package com.devcode.storyappfinal.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.devcode.storyappfinal.data.RepositoryStory
import com.devcode.storyappfinal.db.StoryDatabase
import com.devcode.storyappfinal.model.UserPreferences
import com.devcode.storyappfinal.remote.ApiConfig

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
object Injection {
    fun provideRepository(context: Context): RepositoryStory {
        val storyDatabase = StoryDatabase.getDatabase(context)
        val preferences = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return RepositoryStory(storyDatabase, apiService,preferences)
    }
}