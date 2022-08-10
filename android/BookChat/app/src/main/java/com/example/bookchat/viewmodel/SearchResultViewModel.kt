package com.example.bookchat.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookchat.data.Book
import com.example.bookchat.repository.BookRepository
import com.example.bookchat.utils.SearchOptionType

class SearchResultViewModel(private val repository : BookRepository) : ViewModel() {
    val optionType = MutableLiveData<SearchOptionType>(SearchOptionType.TITLE)
    private val SearchKeyWord : MutableLiveData<String> = MutableLiveData()

    // SearchKeyWord 변경시 자동 갱신되고 getBooks으로 전달된 쿼리를 모았다가 한번에 Range갯수만큼 PagingData리턴
    val pagingData : LiveData<PagingData<Book>> =
        SearchKeyWord.switchMap { queryString ->
            repository.getBooks(queryString)
                .cachedIn(viewModelScope)
    }

    // 라이브 데이터 변경
    fun searchBook(keyWord :String) {
        SearchKeyWord.value = keyWord
    }
}