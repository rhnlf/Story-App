package com.rhnlf.storyapp.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UploadStoryResponse(

    @field:SerializedName("error") val error: Boolean? = null,

    @field:SerializedName("message") val message: String? = null
) : Parcelable