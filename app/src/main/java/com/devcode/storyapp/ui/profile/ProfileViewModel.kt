package com.devcode.storyapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.devcode.storyapp.data.RepositoryStory
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: RepositoryStory) : ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}