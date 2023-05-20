package com.devcode.storyapp.ui.register

import androidx.lifecycle.ViewModel
import com.devcode.storyapp.data.RepositoryStory

class RegisterViewModel(private val repository: RepositoryStory) : ViewModel() {
    fun doingRegister(name: String, email: String, password: String) =
        repository.doingRegister(name, email, password)
}