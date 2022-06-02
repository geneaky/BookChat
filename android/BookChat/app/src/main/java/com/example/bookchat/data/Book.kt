package com.example.bookchat.data

data class Book(
    var isbn: String? = "",
    var title: String? = "",
    var author: String? = "",
    var publisher: String? = "",
    var bookCoverImageUrl: String? = ""
)
