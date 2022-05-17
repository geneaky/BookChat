package com.example.bookchat.api

import com.example.bookchat.data.BookResponse
import com.example.bookchat.data.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookChatService {

    //유저 정보 가져오기
    //헤더에 토큰만 실어보내면 됨
    @GET("/v1/api/users/profile")
    fun getUser() : Call<UserResponse>

    //ISBN으로 도서 검색
    @GET("/v1/api/books")
    fun getBookISBN(
        @Query("isbn") isbn:String
    ): Call<BookResponse>

    //제목으로 도서 검색
    @GET("/v1/api/books")
    fun getBookTITLE(
        @Query("title") title:String
    ): Call<BookResponse>

    //저자명으로 도서 검색
    @GET("/v1/api/books")
    fun getBookAUTHOR(
        @Query("author") author:String
    ): Call<BookResponse>

}