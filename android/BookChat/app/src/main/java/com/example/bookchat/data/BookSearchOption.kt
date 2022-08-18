package com.example.bookchat.data

data class BookSearchOption(
    val keyWord :String,
    val size :String = " ", //Pager에 사전작업 해놓음
    val page :String = " ", //Pager에 사전작업 해놓음
    val sort :String
)