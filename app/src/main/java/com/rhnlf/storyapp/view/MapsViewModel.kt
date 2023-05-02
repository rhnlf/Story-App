package com.rhnlf.storyapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rhnlf.storyapp.data.repository.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getStory() = repository.getStoryWithLocation().asLiveData()
}