package com.example.bookchat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookchat.data.User
import com.example.bookchat.repository.UesrRepository
import com.example.bookchat.utils.Constants.TAG

class UserInforViewModel : ViewModel(){

    private lateinit var mRepository : UesrRepository

    private val _user = MutableLiveData<User>()
    private val _userEmail = MutableLiveData<String>()
    private val _userName =  MutableLiveData<String>()
    private val _userProfileUrl =  MutableLiveData<String>()

    val user : LiveData<User>
        get() = _user
    val userEmail : LiveData<String>
        get() = _userEmail
    val userName : LiveData<String>
        get() = _userName
    val userProfileUrl : LiveData<String>
        get() = _userProfileUrl

    fun activityInitialization(){
        //유저 정보 가져오기
        getUserInfo()

        //유저 도서 가져오기
        
        //유저 채팅방 가져오기
    }
    fun getUserInfo(){
        mRepository = UesrRepository()
        _user.value = mRepository.getUser().also {
            Log.d(TAG, "UserInforViewModel: activityInitialization() - _user.value : ${it}") //이거 텅비어서 오고 있잖아 지금
        }
        if(user.value?.userEmail.isNullOrEmpty()){
            Log.d(TAG, "UserInforViewModel: activityInitialization() - 데이터를 받아오지 못함")
            //데이터를 가져오는건 되는데 세팅이 안되고 있다.
            //LiveData가 똑바로 연결이 안되어 있다는 말임
            //
        }else{
            Log.d(TAG, "UserInforViewModel: activityInitialization() - 데이터를 받아오는 중")
            //값 받아오고 라이브데이터 값 세팅
            _userName.value = user.value?.userName ?: "오류".also {
                Log.d(TAG, "UserInforViewModel: activityInitialization() - _userName.value = ${_userName.value}")
            }
            _userEmail.value = user.value?.userEmail ?: "오류"
            _userProfileUrl.value = user.value?.userProfileImageUri  ?: "오류"
        }
    }

}