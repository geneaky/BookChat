package com.example.bookchat

import android.app.Application
import com.example.bookchat.api.ApiClient
import com.example.bookchat.api.ApiInterface
import com.example.bookchat.utils.NetworkManager

class App : Application() {
    // context를 가지지 않는곳에서 context를 필요로 할 때를 위해서 정의
    //모든 곳에서 접근해야하니까 companion object로 정의
    companion object{
        lateinit var instance : App
            private set //외부 수정 불가
    }

    lateinit var networkManager: NetworkManager
    lateinit var apiInterface :ApiInterface


    //액티비티 , 리시버 , 서비스가 생성되기 전에 어플리케이션이 시작 중일 때 실행됨
    override fun onCreate() {
        super.onCreate()
        instance = this
        inject()
    }

    private fun inject() {
        networkManager = NetworkManager()
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }
}