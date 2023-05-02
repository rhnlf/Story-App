package com.rhnlf.storyapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rhnlf.storyapp.data.remote.response.ListStoryItem
import com.rhnlf.storyapp.data.repository.StoryRepository
import com.rhnlf.storyapp.helper.Event

class MainViewModel(
    repository: StoryRepository
) : ViewModel() {

    private val mIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mIsLoading

    private val mSnackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = mSnackBarText

    val story: LiveData<PagingData<ListStoryItem>> = repository.getStory().cachedIn(viewModelScope)

    companion object {
        private const val TAG = "MainViewModel"
    }
}