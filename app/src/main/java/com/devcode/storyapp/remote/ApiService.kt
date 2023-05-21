package com.devcode.storyapp.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") header: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int? = null
    ): StoryAPIResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): FileUploadResponse

    @Multipart
    @POST("/v1/stories/guest")
    suspend fun uploadStoryAsGuest(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): FileUploadResponse

    @GET("stories")
    suspend fun getLocationUsers(
        @Header("Authorization") header: String,
        @Query("location") location: Int,
    ): StoryAPIResponse
}