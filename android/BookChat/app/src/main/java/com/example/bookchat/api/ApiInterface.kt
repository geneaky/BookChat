package com.example.bookchat.api

import com.example.bookchat.data.BookSearchResultDto
import com.example.bookchat.data.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("/v1/api/users/profile")
    fun getUserProfile() : Call<User>

    //ISBN으로 도서 검색
    @GET("/v1/api/books")
    fun getBookFromIsbn(
        @Query("isbn") isbn:String
    ): Call<BookSearchResultDto>

    //제목으로 도서 검색
    @GET("/v1/api/books")
    fun getBookFromTitle(
        @Query("title") title:String
    ): Call<BookSearchResultDto>

    //저자명으로 도서 검색
    @GET("/v1/api/books")
    fun getBookFromAuthor(
        @Query("author") author:String
    ): Call<BookSearchResultDto>
}