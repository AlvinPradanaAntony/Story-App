package com.devcode.storyapp.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
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

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ) : DetailStoryResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") header: String
    ): StoryAPIResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): FileUploadResponse

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
    suspend fun uploadImageGuest(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<FileUploadResponse>

    @GET("stories")
    suspend fun getLocationUsers(
        @Header("Authorization") header: String,
        @Query("location") location: Int,
    ): StoryAPIResponse

    @GET("stories")
    suspend fun getListStoryWithPaging(
        @Header("Authorization") header: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): StoryAPIResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStoryWithLocation(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitudeUpload: RequestBody? = null,
        @Part("lon") longitudeUpload: RequestBody? = null
    ): FileUploadResponse
}