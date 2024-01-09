package com.devcode.storyappfinal.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcode.storyappfinal.data.RepositoryStory
import com.devcode.storyappfinal.model.UserModel
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