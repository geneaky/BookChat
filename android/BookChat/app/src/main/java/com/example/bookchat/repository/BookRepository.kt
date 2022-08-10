package com.example.bookchat.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.bookchat.App
import com.example.bookchat.Pagingtest.BookSearchResultPagingSource
import com.example.bookchat.data.Book
import com.example.bookchat.data.BookSearchResultDto
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.utils.SearchOptionType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder

class BookRepository {

//    private lateinit var books :ArrayList<Book>
//    private lateinit var call : Call<BookSearchResultDto>

    fun getBooks(title : String) : LiveData<PagingData<BookSearchResultDto>> {
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

//    fun getBooks(searchKeyWord : String,searchOption: SearchOptionType,callback : (ArrayList<Book>) -> Unit){
//
//        when(searchOption){
//            SearchOptionType.TITLE -> {call =  App.instance.apiInterface.getBookFromTitle(searchKeyWord).also {
//                Log.d(TAG, "BookRepository: getBooks() - SearchOptionType : TITLE")
//            }}
//            SearchOptionType.AUTHOR -> {call =  App.instance.apiInterface.getBookFromAuthor(searchKeyWord).also {
//                Log.d(TAG, "BookRepository: getBooks() - SearchOptionType : AUTHOR")
//            }}
//            SearchOptionType.ISBN -> {call =  App.instance.apiInterface.getBookFromIsbn(searchKeyWord).also {
//                Log.d(TAG, "BookRepository: getBooks() - SearchOptionType : ISBN")
//            }}
//        }
//
//        call.enqueue(object :Callback<BookSearchResultDto>{
//            override fun onResponse(
//                call: Call<BookSearchResultDto>,
//                response: Response<BookSearchResultDto>
//            ) {
//                if (response.isSuccessful){
//                    Log.d(TAG, "BookRepository: onResponse() - Success(통신 성공)-response.body() : ${response.body() } , 응답 코드 : ${response.code()}")
//                    books = response.body()!!.books
//
//                    books.forEach{
//                        Log.d(TAG, "SearchResultViewModel: 테스트 : title : it.title , '(' index : ${it.title.indexOf('(')   }")
//                    }
//                    val t= books.map {
//                        if(it.title.indexOf('(') != -1) {
//                            it.title = StringBuilder(it.title).insert(it.title.indexOf('('),"\n").toString()
//                            it
//                        }
//                        else it
//                    }.toCollection(ArrayList())
//
//                    //callback(books)
//                    callback(t)
//                    return
//                }
//                Log.d(TAG, "BookRepository: onResponse() - Fail(통신 실패) 응답 코드 : ${response.code()}")
//            }
//
//            override fun onFailure(
//                call: Call<BookSearchResultDto>,
//                t: Throwable) {
//                //인터넷 끊김 , 예외 발생 등 시스템적 이유로 통신 실패
//                Log.d(TAG, "BookRepository: onFailure() - Throwable : ${t}")
//            }
//
//        })
//    }
}