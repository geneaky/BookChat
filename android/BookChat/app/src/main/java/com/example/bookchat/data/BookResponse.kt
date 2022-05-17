package com.example.bookchat.data

import com.google.gson.annotations.SerializedName

//DTO
data class BookResponse(
    @SerializedName("isbn")
    val isbn: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("bookCoverImageUrl")
    val bookCoverImageUrl: String
)
