package com.devcode.storyappfinal.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcode.storyappfinal.data.RepositoryStory
import com.devcode.storyappfinal.model.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: RepositoryStory) : ViewModel() {
    fun doingLogin(email: String, password: String) = repository.doingLogin(email, password)
    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            repository.saveUserData(user)
        }
    }
}