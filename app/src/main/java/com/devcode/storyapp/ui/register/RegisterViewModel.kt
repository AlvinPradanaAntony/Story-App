package com.devcode.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcode.storyapp.data.RepositoryStory
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.remote.ApiConfig
import com.devcode.storyapp.remote.RegisterResponse
import com.devcode.storyapp.model.UserPreferences
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val repository: RepositoryStory) : ViewModel() {
    fun doingRegister(name: String, email: String, password: String) =
        repository.doingRegister(name, email, password)
}