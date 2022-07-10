package com.example.bookchat.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.bookchat.App
import com.example.bookchat.utils.Constants.TAG

object SharedPreferenceManager {

    const val TOKEN_PREF_KEY = "TOKEN_PREF_KEY"
    //SharedPreferences 인스턴스 생성
    //App.instance = 앱 컨텍스트 받아오기
    val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    val tokenPref = EncryptedSharedPreferences.create(
        "encryptedShared",
        masterKeyAlias,
        App.instance,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

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

    //토큰값 있는지 확인
    fun isTokenEmpty() :Boolean{
        return tokenPref.getString(TOKEN_PREF_KEY,"").isNullOrEmpty()
    }


}