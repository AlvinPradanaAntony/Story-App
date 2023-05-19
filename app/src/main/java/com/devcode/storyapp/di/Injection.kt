package com.devcode.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.devcode.storyapp.data.RepositoryStory
import com.devcode.storyapp.model.UserPreferences
import com.devcode.storyapp.remote.ApiConfig

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
object Injection {
    fun provideRepository(context: Context): RepositoryStory {
        val preferences = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return RepositoryStory(preferences, apiService)
    }
}