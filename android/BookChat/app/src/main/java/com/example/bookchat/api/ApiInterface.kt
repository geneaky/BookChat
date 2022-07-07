package com.example.bookchat.api

import com.example.bookchat.data.User
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    //유저 정보 가져오기
    //헤더에 토큰만 실어보내면 됨 (파라미터 필요 x)
    @GET("/v1/api/users/profile")
    fun getUser() : Call<User>

}