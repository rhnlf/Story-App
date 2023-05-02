package com.rhnlf.storyapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rhnlf.storyapp.data.repository.StoryRepository
import com.rhnlf.storyapp.helper.Event

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {

    private val mIsAnimate = MutableLiveData<Event<Boolean>>()
    val isAnimate: LiveData<Event<Boolean>> = mIsAnimate

    init {
        mIsAnimate.value = Event(false)
    }

    fun registerUser(name: String, email: String, password: String) =
        repository.register(name, email, password).asLiveData()
}