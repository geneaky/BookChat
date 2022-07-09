package com.example.bookchat.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//DTO
data class Book(
    @SerializedName("isbn")
    val isbn: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: ArrayList<String> = ArrayList<String>(), //작가가 공동저자일 수도 있으니 배열로 선언
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("bookCoverImageUrl")
    val bookCoverImageUrl: String
) : Serializable // Intent간 객체 전달을 위해서
