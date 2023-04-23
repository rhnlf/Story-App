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
import com.rhnlf.storyapp.data.remote.response.UploadStoryResponse
import com.rhnlf.storyapp.helper.Event
import com.rhnlf.storyapp.helper.Helper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PostStoryViewModel(
    private val pref: UserPreference, private val application: Application
) : ViewModel() {

    private val mIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mIsLoading

    private val mSnackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = mSnackBarText

    private val mTempFile = MutableLiveData<File>()
    val tempFile: LiveData<File> = mTempFile

    fun setFile(file: File) {
        mTempFile.value = file
    }

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun uploadStory(token: String, desc: String) {
        showLoading(true)

        val file = Helper.reduceFileImage(mTempFile.value as File)
        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo", file.name, requestImageFile
        )

        val client =
            ApiConfig.getApiService().uploadStory(token, imageMultipart, description, null, null)
        client.enqueue(object : Callback<UploadStoryResponse> {
            override fun onResponse(
                call: Call<UploadStoryResponse>, response: Response<UploadStoryResponse>
            ) {
                if (response.isSuccessful) {
                    showLoading(false)
                    val responseBody = response.body()
                    if (responseBody != null) {
                        mSnackBarText.value = Event(responseBody.message as String)
                    }
                } else {
                    mSnackBarText.value =
                        Event(application.getString(R.string.something_went_wrong))
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UploadStoryResponse>, t: Throwable) {
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
        private const val TAG = "AddStoryViewModel"
    }
}