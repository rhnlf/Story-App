package com.rhnlf.storyapp.data.remote.api

import com.rhnlf.storyapp.data.remote.response.LoginResponse
import com.rhnlf.storyapp.data.remote.response.RegisterResponse
import com.rhnlf.storyapp.data.remote.response.StoryResponse
import com.rhnlf.storyapp.data.remote.response.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String, @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @GET("stories")
    fun getAllStory(
        @Header("Authorization") token: String
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<UploadStoryResponse>
}