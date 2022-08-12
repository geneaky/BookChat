package com.example.bookchat.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.bookchat.App
import com.example.bookchat.Paging.BookSearchResultPagingSource
import com.example.bookchat.data.Book
import com.example.bookchat.data.BookSearchOption

class BookRepository {
    fun getBooks(bookSearchOption: BookSearchOption) : LiveData<PagingData<Book>> {
        return Pager(
            config = PagingConfig(pageSize = 20), //한 번에 로드된 항목 수
            pagingSourceFactory = { BookSearchResultPagingSource(App.instance.apiInterface,bookSearchOption) }
        ).liveData
    }
}