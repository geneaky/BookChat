package com.example.bookchat.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookchat.data.Book
import com.example.bookchat.data.BookSearchOption
import com.example.bookchat.repository.BookRepository
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.utils.SearchType

class SearchResultViewModel(private val repository : BookRepository) : ViewModel() {
    private val searchOption : MutableLiveData<BookSearchOption> = MutableLiveData()
    private val searchResultCount : MutableLiveData<Int> = MutableLiveData()

    // SearchKeyWord 변경시 자동 갱신되고 getBooks으로 전달된 쿼리를 모았다가 한번에 Range갯수만큼 PagingData리턴
    val pagingData : LiveData<PagingData<Book>> =
        searchOption.switchMap { bookSearchOption ->
            Log.d(TAG, "SearchResultViewModel: pagingData 초기화!")
            repository.getBooks(bookSearchOption,{callBackValue -> searchResultCount.value = callBackValue})
                .cachedIn(viewModelScope)
    }

    // 라이브 데이터 변경
    fun searchBook(bookSearchOption: BookSearchOption) {
        searchOption.value = bookSearchOption
    }
    fun getResultCount() :Int{
        return searchResultCount.value ?: 0
    }
}