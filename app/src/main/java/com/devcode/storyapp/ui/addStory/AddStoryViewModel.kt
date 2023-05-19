package com.devcode.storyapp.ui.addStory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.devcode.storyapp.data.RepositoryStory
import com.devcode.storyapp.remote.ApiConfig
import com.devcode.storyapp.remote.FileUploadResponse
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val repository: RepositoryStory) : ViewModel() {
    fun addStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: Double?,
                 lon: Double?) = repository.addStory(token, file, description, lat, lon)

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }
}