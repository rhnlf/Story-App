package com.rhnlf.storyapp.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.data.remote.api.ApiConfig
import com.rhnlf.storyapp.data.remote.response.RegisterResponse
import com.rhnlf.storyapp.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val application: Application) : ViewModel() {

    private val mIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mIsLoading

    private val mSnackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = mSnackBarText

    private val mIsUserCreated = MutableLiveData<Boolean>()
    val isUserCreated: LiveData<Boolean> = mIsUserCreated

    init {
        mIsUserCreated.value = false
    }

    fun register(name: String, email: String, password: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>, response: Response<RegisterResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val isError = responseBody.error as Boolean
                        if (isError) mSnackBarText.value =
                            Event(application.getString(R.string.email_is_already_taken))
                        else mIsUserCreated.value = true
                    }
                } else {
                    mSnackBarText.value =
                        Event(application.getString(R.string.email_is_already_taken))
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showLoading(false)
                mSnackBarText.value = Event(application.getString(R.string.something_went_wrong))
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        mIsLoading.value = isLoading
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}