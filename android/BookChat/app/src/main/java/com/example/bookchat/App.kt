package com.example.bookchat

import android.app.Application

//Application을 상속받은 클래스는 1번째 액티비티보다 먼저 인스턴스화된다
//Application을 상속받은 클래스는 공동으로 관리해야 하는 데이터를 작성하기에 적합하다.
//Menifests에 android:name=".App" 앱이름을 등록해줘야 사용가능함 안하면 RuntimeError 발생함
class App : Application() {

    companion object{
        lateinit var instance : App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}