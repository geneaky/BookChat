package com.example.bookchat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookchat.data.BookResponse
import com.example.bookchat.utils.Constants.TAG

class SearchResultBookPageViewModel : ViewModel() {
    private val _book = MutableLiveData<BookResponse>()

    val book : LiveData<BookResponse>
        get() = _book.also {
            Log.d(TAG, "SearchResultBookPageViewModel: () - _book.value : ${_book.value}")
        }
}