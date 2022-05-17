package com.example.bookchat.data

import com.google.gson.annotations.SerializedName

//DTO
data class UserResponse(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userEmail")
    val userEmail: String,
    @SerializedName("userProfileImageUri")
    val userProfileImageUri: String
)
