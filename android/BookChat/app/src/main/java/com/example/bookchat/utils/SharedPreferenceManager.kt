package com.example.bookchat.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.bookchat.App
import com.example.bookchat.utils.Constants.TAG
import com.google.gson.Gson

object SharedPreferenceManager {
    lateinit var pref :SharedPreferences

    //history 가져오기
    fun getSearchHistory() :ArrayList<String>{
        Log.d(TAG, "SharedPreferenceManager: getSearchHistory() - called")
        val sharedPref = App.instance.getSharedPreferences("searchHistory", Context.MODE_PRIVATE) //App.instance = 앱 컨텍스트 받아오기
        val searchHistoryListString = sharedPref.getString("key_search_history","") ?: ""
        Log.d(TAG, "SearchActivity: getSearchHistory()- searchHistoryListString : ${searchHistoryListString}")

        var searchHistoryList = ArrayList<String>()

        if (searchHistoryListString.isNotEmpty()){
            // 문자열 -> 배열로 변환
            searchHistoryList = Gson().
            fromJson(searchHistoryListString, Array<String>::class.java).
            toMutableList() as ArrayList<String>
        }
        return searchHistoryList
    }

    //history 저장하기
    fun setSearchHistory(searchKeyWord : String){
        Log.d(TAG, "SearchActivity: setSearchHistory() - called")

        var searchHistoryList = getSearchHistory() //저장되어있는 검색기록 불러옴
        searchHistoryList.add(searchKeyWord)

        // 배열 -> 문자열(Json)으로 변환
        val searchHistoryListString : String = Gson().toJson(searchHistoryList)
        Log.d(TAG, "SearchActivity: setSearchHistory() - searchHistoryListString : ${searchHistoryListString}") //저장 잘 되는거 확인

        val sharedPref = App.instance.getSharedPreferences("searchHistory",Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("key_search_history",searchHistoryListString)
        editor.apply()
    }

    //history 초기화
    fun clearSearchHistory(){
        Log.d(TAG, "SearchActivity: clearSearchHistory() - called")

        val sharedPref = App.instance.getSharedPreferences("searchHistory",Context.MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
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

        val sharedPref = App.instance.getSharedPreferences("searchHistory",Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("key_search_history",searchHistoryListString)
        editor.apply()
    }

    //토큰 저장하기
    fun saveToken(token :String){
        Log.d(TAG, "SharedPreferenceManager: saveToken() - called")
        pref = App.instance.getSharedPreferences("token", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("token","Bearer $token")
        editor.apply()
    }

    //토큰 가져오기
    fun getToken() :String?{
        return  pref.getString("token","notToken")
    }

}