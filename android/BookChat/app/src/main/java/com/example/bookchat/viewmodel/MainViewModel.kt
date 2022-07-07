package com.example.bookchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookchat.data.User
import com.example.bookchat.repository.UesrRepository

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
            mRepository.getUser{ user: User -> _user.value = user
            println("_user.value : ${_user.value}")}   //user 받아올 콜백 메서드 전달
        }

    }

}