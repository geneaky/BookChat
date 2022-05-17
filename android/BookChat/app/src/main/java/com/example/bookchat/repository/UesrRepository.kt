package com.example.bookchat.repository

import android.util.Log
import com.example.bookchat.api.ApiClient
import com.example.bookchat.api.BookChatService
import com.example.bookchat.data.UserResponse
import com.example.bookchat.utils.Constants.TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UesrRepository{

    //Clean Architecture 리팩토링 하기
    
    fun getUser(){
        val apiClient = ApiClient.getApiClient().create(BookChatService::class.java)
        val call = apiClient.getUser()

        //비동기 작업 시작(response 가져오기 : 비동기) 
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                //메인스레드 작업부분이라 UI 작업가능
                if(response.isSuccessful){
                    Log.d(TAG, "UesrRepository: onResponse() - Success(통신 성공)")

                    Log.d(TAG, "UesrRepository: onResponse() - called response : ${response}")
                    val userName = response.body()?.userName.also {
                        Log.d(TAG, "UesrRepository: onResponse() - userName : ${it}")
                    }
                    val userEmail = response.body()?.userEmail.also {
                        Log.d(TAG, "UesrRepository: onResponse() - userEmail : ${it}")
                    }
                    val userProfile = response.body()?.userProfileImageUri.also {
                        Log.d(TAG, "UesrRepository: onResponse() - userProfile : ${it}")
                    }

                }else{
                    //응답 코드 3xx, 4xx (통신 실패)
                    Log.d(TAG, "UesrRepository: onResponse() - Fail(통신 실패)")
                }

            }

            override fun onFailure(
                call: Call<UserResponse>,
                t: Throwable) {
                //인터넷 끊김 , 예외 발생 등 시스템적 이유로 통신 실패
                Log.d(TAG, "MainActivity: onFailure() - called")
            }

        })
    }


}

//인터셉터 레파지토리 레트로핏 OkHttp 바인딩어댑터
// 코루틴으로 값 가져오기
// 공부해야함
