package com.example.bookchat.api

import com.example.bookchat.data.Book
import com.example.bookchat.data.BookSearchResultDto
import com.example.bookchat.data.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("/v1/api/users/profile")
    fun getUserProfile() : Call<User>

    //검색어랑 태그로 구분하는게 차라리 깔끔할 거 같은데

    //ISBN으로 도서 검색
    @GET("/v1/api/books")
    fun getBookFromIsbn(
        @Query("isbn") isbn:String
    ): Call<BookSearchResultDto>

    //제목으로 도서 검색
    @GET("/v1/api/books")
    suspend fun getBookFromTitle(
        @Query("title") title:String,
        @Query("size") size:String,
        @Query("page") page:String,
        @Query("sort") sort:String,
    ): Response<BookSearchResultDto>

    //저자명으로 도서 검색
    @GET("/v1/api/books")
    fun getBookFromAuthor(
        @Query("author") author:String
    ): Call<BookSearchResultDto>
}