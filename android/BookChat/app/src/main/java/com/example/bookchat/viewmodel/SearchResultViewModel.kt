package com.example.bookchat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookchat.data.BookResponse
import com.example.bookchat.repository.BookRepository
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.utils.SearchOptionType

class SearchResultViewModel() : ViewModel(){

    private lateinit var mRepository: BookRepository
    private val _isLoading = MutableLiveData<Boolean>(false)
    private val _books = MutableLiveData<ArrayList<BookResponse>>()
    private val _optionType = MutableLiveData<SearchOptionType>(SearchOptionType.TITLE)

    val isLoading: LiveData<Boolean>
        get() =_isLoading
    val books : LiveData<ArrayList<BookResponse>>
        get() = _books.also {
            Log.d(TAG, "SearchResultViewModel: () - _books.value : ${_books.value}")
        }
    val optionType: LiveData<SearchOptionType>
        get() = _optionType

    fun getBooks(searchKeyWord : String,
                 searchOption: SearchOptionType,
                 success : () -> Unit){
        _isLoading.value = true //로딩 중

        _optionType.value = searchOption //한 번 검색했으면 옵션값 저장

        mRepository = BookRepository()
        //books 배열 받아올 콜백 메서드 전달
        mRepository.getBooks(searchKeyWord ,searchOption){books : ArrayList<BookResponse> -> _books.value = books}
        success()

        _isLoading.value = false //로딩 끝

        //근데 어차피 이 코드를 실행하고 기다려도 최종적으로 통신을 해서 데이터를 받아오는 놈이 다른 스레드라서,
        //지금 현재 스레드가 저 스레드를 위해서 blocking해서 기다리거나 아니면 계속해서 저놈이 데이터가 들어왔는지 확인하거나,
        //아니면 데이터가 도착했을때 옵저버 패턴을 이용해서 메인스레드에게 알려주거나 이 세가지 방법밖에는 없는거 같은데
        //다른 스레드가 작업하기 때문에 데이터를 받아오는 것과 지금 시간차가 있음

        //여기다가 코루틴을 쓰거나 콜백함수를 넘겨주는건 어떨까?
    }

}