package com.rhnlf.storyapp.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.data.local.User
import com.rhnlf.storyapp.data.local.UserPreference
import com.rhnlf.storyapp.data.remote.api.ApiConfig
import com.rhnlf.storyapp.data.remote.response.LoginResponse
import com.rhnlf.storyapp.data.remote.response.LoginResult
import com.rhnlf.storyapp.helper.Event
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(
    private val pref: UserPreference, private val application: Application
) : ViewModel() {

    private val mIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mIsLoading

    private val mLoginError = MutableLiveData<Boolean>()
    val loginError: LiveData<Boolean> = mLoginError

    private val mSnackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = mSnackBarText

    fun login(email: String, password: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>, response: Response<LoginResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        mLoginError.value = responseBody.error as Boolean
                        val loginResult = responseBody.loginResult as LoginResult
                        val name = loginResult.name as String
                        val token = loginResult.token as String
                        viewModelScope.launch {
                            pref.saveUser(User(name, email, token))
                        }
                    }
                } else {
                    mLoginError.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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
        private const val TAG = "LoginViewModel"
    }
}