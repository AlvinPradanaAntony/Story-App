package com.devcode.storyapp.ui.addStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.devcode.storyapp.data.RepositoryStory
import com.devcode.storyapp.model.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: RepositoryStory) : ViewModel() {
    fun addStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: Double?,
                 lon: Double?) = repository.addStory(token, file, description, lat, lon)

    fun addStoryAsGuest(file: MultipartBody.Part, description: RequestBody, lat: Double?,
                 lon: Double?) = repository.addStoryAsGuest(file, description, lat, lon)

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }
}