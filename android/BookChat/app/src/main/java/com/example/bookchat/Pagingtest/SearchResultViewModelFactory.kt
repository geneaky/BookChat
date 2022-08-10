package com.example.bookchat.Pagingtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookchat.repository.BookRepository
import com.example.bookchat.viewmodel.SearchResultViewModel

// 뷰모델에 인자를 넘겨주기 위한 팩토리 메서드
class SearchResultViewModelFactory(
    private val repository : BookRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchResultViewModel(repository) as T
    }
}