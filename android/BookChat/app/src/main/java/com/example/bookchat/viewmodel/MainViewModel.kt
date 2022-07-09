package com.example.bookchat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookchat.data.User
import com.example.bookchat.repository.UesrRepository
import com.example.bookchat.utils.Constants.TAG

class MainViewModel : ViewModel(){

    private lateinit var mRepository : UesrRepository
    private val _user = MutableLiveData<User>()

    val user : LiveData<User>
        get() = _user


    fun activityInitialization(){
        //유저 정보 가져오기
        getUserInfo()

        //유저 도서 가져오기
        
        //유저 채팅방 가져오기
    }
    fun getUserInfo(){
        mRepository = UesrRepository()
        if (_user.value == null){
            mRepository.getUserProfile{ user: User -> _user.value = user}   //user 받아올 콜백 메서드 전달
            Log.d(TAG, "MainViewModel: getUserInfo() - 값 불러오기 완료")
        }

    }

}