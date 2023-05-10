package com.devcode.storyapp.ui.addStory

import android.util.Log
import androidx.lifecycle.*
import com.devcode.storyapp.db.ApiConfig
import com.devcode.storyapp.db.FileUploadResponse
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _isAddStory = MutableLiveData<FileUploadResponse>()
    val isAddStory: LiveData<FileUploadResponse> = _isAddStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun postAddStory(token: String, imageMultipart: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadImage("Bearer $token", imageMultipart, description)
        client.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>,
                response: Response<FileUploadResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isAddStory.postValue(response.body())
                    }
                } else {
                    _isLoading.value = false
                    val responseError = response.errorBody()?.string()
                    val objErr = JSONObject(responseError.toString())
                    _isError.value = objErr.getString("message")?: response.message()
                    Log.d("PostLoginOnResponse", "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
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