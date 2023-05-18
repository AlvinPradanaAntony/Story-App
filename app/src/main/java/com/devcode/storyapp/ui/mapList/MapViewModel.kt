package com.devcode.storyapp.ui.mapList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.devcode.storyapp.db.ApiConfig
import com.devcode.storyapp.db.StoryAPIResponse
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _isLocationUser = MutableLiveData<StoryAPIResponse>()
    val isLocationUser: LiveData<StoryAPIResponse> = _isLocationUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun postLocation(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getLocationUsers("Bearer $token", 1)
        client.enqueue(object : Callback<StoryAPIResponse> {
            override fun onResponse(
                call: Call<StoryAPIResponse>,
                response: Response<StoryAPIResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isLocationUser.postValue(response.body())
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