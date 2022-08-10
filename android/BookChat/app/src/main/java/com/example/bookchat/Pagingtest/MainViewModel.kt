package com.example.bookchat.Pagingtest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn


// 데이터를 처리함
class MainViewModel(private val repository : MyPagingRepository) : ViewModel() {

    private val myCustomPosts2 : MutableLiveData<String> = MutableLiveData()

    // 라이브 데이터 변경 시 다른 라이브 데이터 발행
    val result = myCustomPosts2.switchMap { queryString ->
        repository.getBooks(queryString).cachedIn(viewModelScope)
    }

    // 라이브 데이터 변경
    fun searchBook(title :String) {
        myCustomPosts2.value = title
    }
}