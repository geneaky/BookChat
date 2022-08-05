package com.example.bookchat.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.bookchat.App
import com.example.bookchat.utils.Constants.TAG
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

object SharedPreferenceManager {

    const val TOKEN_PREF_KEY = "TOKEN_PREF_KEY"
    const val SEARCH_HISTORY_PREF = "SEARCH_HISTORY_PREF"
    const val SEARCH_HISTORY_PREF_KEY = "SEARCH_HISTORY_PREF_KEY"

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
    private var historyPref :SharedPreferences =
        App.instance.getSharedPreferences(SEARCH_HISTORY_PREF, Context.MODE_PRIVATE)

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

    //history 가져오기
    fun getSearchHistory() :ArrayList<String>{
        Log.d(TAG, "SharedPreferenceManager: getSearchHistory() - called")
        val searchHistoryListString = historyPref.getString(SEARCH_HISTORY_PREF_KEY,"") ?: ""
        Log.d(TAG, "SearchActivity: getSearchHistory()- searchHistoryListString : ${searchHistoryListString}")
        var searchHistoryList = ArrayList<String>()

        if (searchHistoryListString.isNotEmpty()){
            // 문자열 -> 배열로 변환
            searchHistoryList = Gson().
            fromJson(searchHistoryListString, Array<String>::class.java).
            toMutableList() as ArrayList<String>
        }
        return searchHistoryList.toCollection(ArrayList())
    }

    //history 저장하기
    fun setSearchHistory(searchKeyWord : String){
        Log.d(TAG, "SearchActivity: setSearchHistory() - called")

        val searchHistoryList = getSearchHistory() //저장되어있는 검색기록 불러옴
        val temp = ArrayList<String>()
        temp.add(searchKeyWord)
        temp.addAll(searchHistoryList)

        // 배열 -> 문자열(Json)으로 변환
        val newHistoryString : String = Gson().toJson(temp)
        Log.d(TAG, "SearchActivity: setSearchHistory() - searchHistoryListString : ${newHistoryString}") //저장 잘 되는거 확인

        val editor = historyPref.edit()
        editor.putString(SEARCH_HISTORY_PREF_KEY,newHistoryString)
        editor.apply()
    }

    //history 초기화
    fun clearSearchHistory(){
        Log.d(TAG, "SearchActivity: clearSearchHistory() - called")

        val editor = historyPref.edit()
        editor.clear()
        editor.apply()
    }

    //history 덮어쓰기
    fun overWriteHistory(searchHistoryList : ArrayList<String>){
        Log.d(TAG, "SharedPreferenceManager: overWiteHistory() - called")
        val sublist = ArrayList<String>()
        searchHistoryList.forEach {
            if (it.isNotEmpty()) sublist.add(it)
        }
        Log.d(TAG, "SharedPreferenceManager: overWriteHistory() - sublist크기 : ${sublist}")

        // 배열 -> 문자열(Json)으로 변환
        val searchHistoryListString : String = Gson().toJson(sublist)
        Log.d(TAG, "SearchActivity: setSearchHistory() - searchHistoryListString : ${searchHistoryListString}") //저장 잘 되는거 확인

        val editor = historyPref.edit()
        editor.putString(SEARCH_HISTORY_PREF_KEY,searchHistoryListString)
        editor.apply()
    }

}