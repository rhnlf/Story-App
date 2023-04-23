package com.rhnlf.storyapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rhnlf.storyapp.data.local.User
import com.rhnlf.storyapp.data.local.UserPreference

class SplashViewModel(private val pref: UserPreference) : ViewModel() {
    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }
}