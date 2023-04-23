package com.rhnlf.storyapp.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.data.local.User
import com.rhnlf.storyapp.data.local.UserPreference
import com.rhnlf.storyapp.data.remote.api.ApiConfig
import com.rhnlf.storyapp.data.remote.response.ListStoryItem
import com.rhnlf.storyapp.data.remote.response.StoryResponse
import com.rhnlf.storyapp.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(
    private val pref: UserPreference, private val application: Application
) : ViewModel() {

    private val mIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mIsLoading

    private val mListStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = mListStory

    private val mSnackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = mSnackBarText

    private fun showLoading(isLoading: Boolean) {
        mIsLoading.value = isLoading
    }

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun getAllStory(token: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().getAllStory(token)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>, response: Response<StoryResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        mListStory.value = responseBody.listStory as List<ListStoryItem>
                    }
                } else {
                    mSnackBarText.value =
                        Event(application.getString(R.string.something_went_wrong))
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                showLoading(false)
                mSnackBarText.value = Event(application.getString(R.string.something_went_wrong))
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}