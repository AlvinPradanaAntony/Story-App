package com.devcode.storyapp.ui.mapList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.devcode.storyapp.data.RepositoryStory
import com.devcode.storyapp.model.UserModel

class MapViewModel(private val repository: RepositoryStory) : ViewModel() {
    fun getStoryLocation(token: String) = repository.getLocationUser(token)

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }

}