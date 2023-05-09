package com.devcode.storyapp.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.devcode.storyapp.db.ApiConfig
import com.devcode.storyapp.db.LoginResponse
import com.devcode.storyapp.db.StoryAPIResponse
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _isStory = MutableLiveData<StoryAPIResponse>()
    val isStory: LiveData<StoryAPIResponse> = _isStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun postStory(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStories("Bearer $token")
        client.enqueue(object : Callback<StoryAPIResponse> {
            override fun onResponse(
                call: Call<StoryAPIResponse>,
                response: Response<StoryAPIResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isStory.postValue(response.body())
                    }
                } else {
                    _isLoading.value = false
                    val responseError = response.errorBody()?.string()
                    val objErr = JSONObject(responseError.toString())
                    _isError.value = objErr.getString("message")?: response.message()
                    Log.d("PostLoginOnResponse", "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryAPIResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = t.message
                Log.d("PostLoginOnFailure", "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

}