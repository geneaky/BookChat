package com.example.bookchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookchat.repository.TestRepository

class MainViewModel : ViewModel(){

    private val mRepository : TestRepository by lazy {
        TestRepository()
    }

    private val _userName = MutableLiveData<String>()
    private val _userEmail = MutableLiveData<String>()
    private val _userBooks = MutableLiveData<ArrayList<String>>()
    private val _userChatrooms = MutableLiveData<ArrayList<String>>()

    val userName : LiveData<String>
        get() = _userName
    val userEmail : LiveData<String>
        get() = _userEmail
    val userBook : LiveData<ArrayList<String>>
        get() = _userBooks
    val userChatrooms : LiveData<ArrayList<String>>
        get() = _userChatrooms


    init {
        _userName.value = "로딩"
        _userEmail.value = "loading123@gmail.com"
    }

    fun getUserInfo(){
        //Repository함수 호출
        //    ex : repository.getMemberInfo())
        val infoList = mRepository.getUsetInfo()
        //값 받아오고 라이브데이터 값 세팅
        _userName.value = infoList.get(0)
        _userEmail.value = infoList.get(0)
        _userBooks.value = infoList
        _userChatrooms.value = infoList
    }

}