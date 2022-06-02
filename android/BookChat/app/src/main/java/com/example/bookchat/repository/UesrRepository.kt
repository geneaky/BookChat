package com.example.bookchat.repository

import android.util.Log
import android.widget.Toast
import com.example.bookchat.App
import com.example.bookchat.data.User
import com.example.bookchat.data.UserResponse
import com.example.bookchat.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UesrRepository{

    //Clean Architecture 리팩토링 하기
    
    fun getUser(callback : (User) -> Unit){

        if(App.instance.networkManager.checkNetworkState()){ //네트워크 연결 체크

            val call = App.instance.apiInterface.getUser()

            //비동기 작업 시작(response 가져오기 : 비동기) 메인은 실행시키고 빠짐
            //고로 값을 받아오는 처리를 해야함 (콜백 메서드로 처리함) 혹은 코루틴으로 async await 해도 됨
            call.enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    //메인스레드 작업부분이라 UI 작업가능
                    if(response.isSuccessful){
                        Log.d(Constants.TAG, "UesrRepository: onResponse() - Success(통신 성공)")

                        val user = User()
                        user.userEmail = response.body()?.userEmail.also {
                            Log.d(Constants.TAG, "UesrRepository: onResponse() - userEmail : ${it}")
                        }
                        user.userName = response.body()?.userName.also {
                            Log.d(Constants.TAG, "UesrRepository: onResponse() - userName : ${it}")
                        }
                        user.userProfileImageUri = response.body()?.userProfileImageUri.also {
                            Log.d(Constants.TAG, "UesrRepository: onResponse() - userProfile : ${it}")
                        }

                        callback(user) //user객체 UserInfoViewModel로 반환

                    }else{
                        //응답 코드 3xx, 4xx (통신 실패)
                        Log.d(Constants.TAG, "UesrRepository: onResponse() - Fail(통신 실패)")
                    }

                }

                override fun onFailure(
                    call: Call<UserResponse>,
                    t: Throwable) {
                    //인터넷 끊김 , 예외 발생 등 시스템적 이유로 통신 실패
                    Log.d(Constants.TAG, "MainActivity: onFailure() - Throwable : ${t} ")
                }

            })

        }else{
            //네트워크가 연결되어있지 않음
            Toast.makeText(App.instance.applicationContext,"네트워크가 연결되어 있지 않습니다. \n네트워크를 연결해주세요.", Toast.LENGTH_SHORT).show()
        }




    }


}

//인터셉터 레파지토리 레트로핏 OkHttp 바인딩어댑터
// 코루틴으로 값 가져오기
// 공부해야함
