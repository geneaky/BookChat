package com.example.bookchat.api

import com.example.bookchat.data.User
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("/v1/api/users/profile")
    fun getUserProfile() : Call<User>
}