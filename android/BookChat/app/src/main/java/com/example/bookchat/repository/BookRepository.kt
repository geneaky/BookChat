package com.example.bookchat.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.bookchat.App
import com.example.bookchat.Pagingtest.BookSearchResultPagingSource
import com.example.bookchat.data.Book

class BookRepository {

//    private lateinit var books :ArrayList<Book>
//    private lateinit var call : Call<BookSearchResultDto>

    fun getBooks(title : String) : LiveData<PagingData<Book>> {
        //Pager를 사용하여 데이터를 변환해준다.
        //PagingConfig로 PagingSource 구성 방법을 정의
        //pageSize는 미리 로드할 데이터 개수 값 (일반적으로 보이는 항목의 여러배로 설정)
        //maxSize는 페이지를 삭제하기 전에 PagingData 에 로드 할 수있는 최대 항목 수
        //pagingSourceFactory = pagingSource를 생성할 때 사용할 PagingSource 지정
        //(maxSize는 최소 pageSize + (2 * prefetchDistance) 보다 높게 설정해야 한다.)
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 40,
                enablePlaceholders = false
            ),
            // 사용할 메소드 선언
            pagingSourceFactory = { BookSearchResultPagingSource(App.instance.apiInterface,title) }
        ).liveData
    }

}