package com.devcode.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcode.storyapp.data.RepositoryStory
import com.devcode.storyapp.model.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: RepositoryStory) : ViewModel() {
    fun doingLogin(email: String, password: String) = repository.doingLogin(email, password)
    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            repository.saveUserData(user)
        }
    }
}