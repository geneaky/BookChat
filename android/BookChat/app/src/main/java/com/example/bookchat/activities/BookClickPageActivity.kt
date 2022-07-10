package com.example.bookchat.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bookchat.R
import com.example.bookchat.data.Book
import com.example.bookchat.databinding.ActivityBookClickPageBinding
import com.example.bookchat.viewmodel.SearchResultBookPageViewModel

class BookClickPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBookClickPageBinding
    private lateinit var searchResultBookPageViewModel: SearchResultBookPageViewModel
    lateinit var book :Book

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_click_page)

        with(binding){
            lifecycleOwner = this@BookClickPageActivity
            activity = this@BookClickPageActivity
            searchResultBookPageViewModel = SearchResultBookPageViewModel()
            viewModel = searchResultBookPageViewModel

        }
        book = intent.getSerializableExtra("clickedBook") as Book


    }
}