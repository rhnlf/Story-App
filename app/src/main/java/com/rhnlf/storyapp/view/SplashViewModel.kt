package com.rhnlf.storyapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rhnlf.storyapp.data.local.User
import com.rhnlf.storyapp.data.repository.StoryRepository

class SplashViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getUser(): LiveData<User> = repository.getUser()

}