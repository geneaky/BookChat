package com.example.bookchat.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.bookchat.App
import com.example.bookchat.utils.Constants.TAG

object SharedPreferenceManager {

    const val TOKEN_PREF = "TOKEN_PREF"
    const val TOKEN_PREF_KEY = "TOKEN_PREF_KEY"

    //SharedPreferences 인스턴스 생성
    //App.instance = 앱 컨텍스트 받아오기
    private var tokenPref :SharedPreferences =
        App.instance.getSharedPreferences(TOKEN_PREF, Context.MODE_PRIVATE)

    //토큰 저장하기
    fun saveToken(token :String){
        Log.d(TAG, "SharedPreferenceManager: saveToken() - called")
        val editor = tokenPref.edit()
        editor.putString(TOKEN_PREF_KEY,"Bearer $token")
        editor.apply()
    }

    //토큰 가져오기
    fun getToken() :String?{
        return  tokenPref.getString(TOKEN_PREF_KEY,"")
    }

}