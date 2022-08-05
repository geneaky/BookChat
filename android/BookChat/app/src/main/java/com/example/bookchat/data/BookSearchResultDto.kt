package com.example.bookchat.data

import com.google.gson.annotations.SerializedName

data class BookSearchResultDto(
    @SerializedName("bookDtos")
    val books :ArrayList<Book>,
    @SerializedName("meta")
    val meta : Meta
)
