package com.devcode.storyapp.db

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") header: String
    ) : Call<StoryAPIResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<FileUploadResponse>

    @Multipart
    @POST("/v1/stories/guest")
    fun uploadImageGuest(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<FileUploadResponse>

    @GET("stories")
    fun getLocationUsers(
        @Header("Authorization") header: String,
        @Query("location") location: Int,
    ) : Call<StoryAPIResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStoryWithLocation(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitudeUpload: RequestBody? = null,
        @Part("lon") longitudeUpload: RequestBody? = null
    ) : FileUploadResponse
}