package com.rhnlf.storyapp.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String, val email: String, val token: String
) : Parcelable