package com.example.myapplication

import retrofit2.Call
import retrofit2.http.*


data class LoginRequest(
    val username: String,
    val password: String
)

data class RegistrationRequest(
    val username: String,
    val email: String,
    val birthdate: String,
    val password: String
)

data class PostListResponse(
    val result: List<PostItem>
)

interface ApiService {
    @POST("login/")
    fun login(@Body loginRequest: LoginRequest): Call<User>

    @POST("register/")
    fun register(@Body registrationRequest: RegistrationRequest): Call<User>

    @GET("posts/")
    fun getPosts(): Call<PostListResponse>

    @POST("posts/")
    fun addPost(@Body postItem: PostItem): Call<PostItem>

    @PUT("posts/{identifier}/")
    fun editPost(@Path("identifier") identifier: String?, @Body postItem: PostItem): Call<PostItem>

    @DELETE("posts/{identifier}/")
    fun deletePost(@Path("identifier") identifier: String?): Call<PostItem>
}