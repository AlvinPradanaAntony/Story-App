package com.devcode.storyapp.ui.home

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.devcode.storyapp.data.RepositoryStory
import com.devcode.storyapp.db.StoryResponseRoom
import com.devcode.storyapp.remote.ApiConfig
import com.devcode.storyapp.remote.StoryAPIResponse
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import com.devcode.storyapp.remote.ListStoryItem
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: RepositoryStory) : ViewModel() {
    fun getStory() : LiveData<PagingData<StoryResponseRoom>> =
        repository.getStory().cachedIn(viewModelScope)

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }
}