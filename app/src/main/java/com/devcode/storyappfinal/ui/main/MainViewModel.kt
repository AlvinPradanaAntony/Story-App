package com.devcode.storyappfinal.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.devcode.storyappfinal.data.RepositoryStory
import com.devcode.storyappfinal.db.StoryResponseRoom
import com.devcode.storyappfinal.model.UserModel

class MainViewModel(private val repository: RepositoryStory) : ViewModel() {
    fun getStory() : LiveData<PagingData<StoryResponseRoom>> =
        repository.getStory().cachedIn(viewModelScope)

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }
}