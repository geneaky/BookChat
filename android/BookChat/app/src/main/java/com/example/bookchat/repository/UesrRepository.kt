package com.example.bookchat.repository

import android.util.Log
import android.widget.Toast
import com.example.bookchat.App
import com.example.bookchat.data.User
import com.example.bookchat.utils.Constants.TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UesrRepository{

    fun getUser(callback : (User) -> Unit){
        if(App.instance.networkManager.checkNetworkState()){ //네트워크 연결 체크
            val call = App.instance.apiInterface.getUser()
            call.enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    //메인스레드 작업부분이라 UI 작업가능
                    if(response.isSuccessful){
                        Log.d(TAG, "UesrRepository: onResponse() - Success(통신 성공)")
                        Log.d(TAG, "UesrRepository: onResponse() - response.body() : ${response.body()}")
                        val user = response.body()!!.also { println("USER : $it") }
                        callback(user) //user객체 UserInfoViewModel로 반환
                    }else{
                        Log.d(TAG, "UesrRepository: onResponse() - Fail(통신 실패) 응답 코드: 3xx, 4xx ")
                    }
                }
                override fun onFailure(
                    call: Call<User>,
                    t: Throwable) {
                    //인터넷 끊김 , 예외 발생 등 시스템적 이유로 통신 실패
                    Log.d(TAG, "MainActivity: onFailure() - Throwable : ${t} ")
                }
            })
        }else{
            //네트워크가 연결되어있지 않음
            Toast.makeText(App.instance.applicationContext,"네트워크가 연결되어 있지 않습니다. \n네트워크를 연결해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}

