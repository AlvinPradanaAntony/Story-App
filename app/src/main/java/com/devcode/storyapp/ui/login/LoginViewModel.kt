package com.devcode.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.*
import com.devcode.storyapp.db.ApiConfig
import com.devcode.storyapp.db.LoginResponse
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _loginUser = MutableLiveData<LoginResponse>()
    val loginUser: LiveData<LoginResponse> = _loginUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun postLogin(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _loginUser.postValue(response.body())
                    }
                } else {
                    val responseError = response.errorBody()?.string()
                    val objErr = JSONObject(responseError.toString())
                    _isError.value = objErr.getString("message")?: response.message()
                    Log.d("PostLoginOnResponse", "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = t.message
                Log.d("PostLoginOnFailure", "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}