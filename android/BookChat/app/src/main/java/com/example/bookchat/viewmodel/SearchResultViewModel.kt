package com.example.bookchat.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookchat.data.Book
import com.example.bookchat.data.BookSearchOption
import com.example.bookchat.repository.BookRepository

class SearchResultViewModel(private val repository : BookRepository) : ViewModel() {
    private val searchOption : MutableLiveData<BookSearchOption> = MutableLiveData()
    private val searchResultCount : MutableLiveData<Int> = MutableLiveData()

    // SearchKeyWord 변경시 자동 갱신 Range크기만큼의 PagingData리턴
    val pagingData : LiveData<PagingData<Book>> =
        searchOption.switchMap { bookSearchOption ->
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