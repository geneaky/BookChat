package com.example.bookchat.data

import com.google.gson.annotations.SerializedName

data class BookSearchResultDto(
    @SerializedName("bookDtos")
    val books :List<Book>,
    @SerializedName("meta")
    val meta : Meta
){
    fun isEnd() :Boolean{
        return meta.isEnd
    }
}
