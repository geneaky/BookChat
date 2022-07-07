package com.example.bookchat.data

import com.google.gson.annotations.SerializedName

//DTO
data class User(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userEmail")
    val userEmail: String,
    @SerializedName("userProfileImageUri")
    val userProfileImageUri: String
)
