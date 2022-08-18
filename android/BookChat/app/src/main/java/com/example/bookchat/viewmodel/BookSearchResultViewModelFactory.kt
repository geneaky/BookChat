package com.example.bookchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookchat.repository.BookRepository

// 뷰모델에 인자를 넘겨주기 위한 팩토리 메서드
class BookSearchResultViewModelFactory(
    private val repository : BookRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchResultViewModel(repository) as T
    }
}