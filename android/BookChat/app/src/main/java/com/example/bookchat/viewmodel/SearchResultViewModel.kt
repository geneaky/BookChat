package com.example.bookchat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookchat.data.Book
import com.example.bookchat.repository.BookRepository
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.utils.SearchOptionType

class SearchResultViewModel() : ViewModel(){

    private lateinit var mRepository: BookRepository
    private val _isLoading = MutableLiveData<Boolean>(false)
    private val _books = MutableLiveData<ArrayList<Book>>()
    private val _optionType = MutableLiveData<SearchOptionType>(SearchOptionType.TITLE)

    val isLoading: LiveData<Boolean>
        get() =_isLoading
    val books : LiveData<ArrayList<Book>>
        get() = _books
    val optionType: LiveData<SearchOptionType>
        get() = _optionType

    fun getBooks(searchKeyWord : String,
                 searchOption: SearchOptionType,
                 success : () -> Unit,
                 fail : () -> Unit){
        Log.d(TAG, "SearchResultViewModel: getBooks() - 초기 _books 값 : ${_books.value}")
        _isLoading.value = true //로딩 중
        _optionType.value = searchOption //한 번 검색했으면 옵션값 저장
        mRepository = BookRepository()
        //books 배열 받아올 콜백 메서드 전달
        mRepository.getBooks(searchKeyWord ,searchOption){books ->
            if(books.isNullOrEmpty()) { //결과 없음 화면 출력
                fail()
                _books.value = books
            }
            else {
                success() //리사이클러뷰 갱신
                _books.value = books.also { Log.d(TAG, "SearchResultViewModel: getBooks() - 가져온 _books 값 : ${it}") }
            }
            _isLoading.value = false //로딩 끝
        }
    }

}