package com.rhnlf.storyapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhnlf.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: StoryRepository) : ViewModel() {

    fun logout() = viewModelScope.launch { repository.deleteUser() }

}