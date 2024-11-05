package com.zuba.stroyapp2.network

import com.zuba.stroyapp2.response.ListStory
import com.zuba.stroyapp2.response.LoginRequest
import com.zuba.stroyapp2.response.LoginResponse
import com.zuba.stroyapp2.response.RegisterResponse
import com.zuba.stroyapp2.response.RegistrasiRequest
import com.zuba.stroyapp2.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    fun register(
        @Body registrasiRequest: RegistrasiRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>


    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0
    ): Call<StoryResponse>

    @GET("stories/{id}")
    fun getStory(
        @Path("id") id: String,
        @Header("Authorization") header: String,
    ): Call<StoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") header: String,
    ) : Call<StoryResponse>
}

