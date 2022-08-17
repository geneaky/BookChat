package com.example.bookchat.api

import com.example.bookchat.data.BookSearchResultDto
import com.example.bookchat.data.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("/v1/api/users/profile")
    fun getUserProfile() : Call<User>

    //제목으로 도서 검색 => 통합 쿼리 검색 api로 수정해야함
    @GET("/v1/api/books")
    suspend fun getBookFromTitle(
        @Query("title") title:String,
        @Query("size") size:String,
        @Query("page") page:String,
        @Query("sort") sort:String,
    ): Response<BookSearchResultDto>

}