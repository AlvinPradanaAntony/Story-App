package com.devcode.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.devcode.storyapp.db.StoryDatabase
import com.devcode.storyapp.db.StoryResponseRoom
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import com.devcode.storyapp.remote.*
import com.devcode.storyapp.utils.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class RepositoryStory(private val storyDatabase: StoryDatabase,  private val apiService: ApiService, private val pref: UserPreferences,) {

    fun getStory(): LiveData<PagingData<StoryResponseRoom>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, pref),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun doingLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            if (e is HttpException) {
                val response = Gson().fromJson<LoginResponse>(e.response()?.errorBody()?.charStream(),
                    object : TypeToken<LoginResponse>() {}.type
                )
                emit(Result.Error(response.message ?: "Unknown error occurred"))
            } else {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun doingRegister(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, password)
                emit(Result.Success(response))
            } catch (e: Exception) {
                if (e is HttpException) {
                    val response = Gson().fromJson<RegisterResponse>(e.response()?.errorBody()?.charStream(),
                        object : TypeToken<RegisterResponse>() {}.type
                    )
                    emit(Result.Error(response.message ?: "Unknown error occurred"))
                } else {
                    emit(Result.Error(e.message.toString()))
                }
            }
        }

    fun addStory(token: String, imageMultipart: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?): LiveData<Result<FileUploadResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadStory(token, imageMultipart, description,lat, lon)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getLocationUser(token: String): LiveData<Result<StoryAPIResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getLocationUsers(token, 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveUserData(user: UserModel) {
        pref.saveUser(user)
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    suspend fun logout() {
        pref.logout()
    }
}