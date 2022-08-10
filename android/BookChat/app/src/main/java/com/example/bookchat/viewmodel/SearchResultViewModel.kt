package com.example.bookchat.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookchat.Pagingtest.MyPagingRepository
import com.example.bookchat.data.Book
import com.example.bookchat.data.BookSearchResultDto
import com.example.bookchat.repository.BookRepository
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.utils.SearchOptionType

class SearchResultViewModel(private val repository : BookRepository) : ViewModel() {

    private lateinit var mRepository: BookRepository
    private val _books = MutableLiveData<ArrayList<Book>>()
    private val _optionType = MutableLiveData<SearchOptionType>(SearchOptionType.TITLE)

    val books : LiveData<ArrayList<Book>>
        get() = _books
    val optionType: LiveData<SearchOptionType>
        get() = _optionType

    private val myCustomPosts2 : MutableLiveData<String> = MutableLiveData()

    // 라이브 데이터 변경 시 다른 라이브 데이터 발행
    //제목을 넣고 돌려받은 PagingData를 담은 LiveData (제목이 바뀌면 자동 갱신된다.)
    //페이징된 책을을 가지고 온다. (1개가 아니라 정해진 Range갯수 만큼!)
    val result : LiveData<PagingData<BookSearchResultDto>> =
        myCustomPosts2.switchMap { queryString ->
            repository.getBooks(queryString)
                //.cachedIn(viewModelScope)
    }

    // 라이브 데이터 변경
    fun searchBook(title :String) {
        myCustomPosts2.value = title
    }


//    fun getBooks(searchKeyWord : String,
//                 searchOption: SearchOptionType,
//                 success : () -> Unit,
//                 fail : () -> Unit){
//        Log.d(TAG, "SearchResultViewModel: getBooks() - 초기 _books 값 : ${_books.value}")
//        _isLoading.value = true //로딩 중
//        _optionType.value = searchOption //한 번 검색했으면 옵션값 저장
//        mRepository = BookRepository()
//        //books 배열 받아올 콜백 메서드 전달
//        mRepository.getBooks(searchKeyWord ,searchOption){books ->
//            if(books.isNullOrEmpty()) { //결과 없음 화면 출력
//                fail()
//                _books.value = books
//            }
//            else {
//                success() //리사이클러뷰 갱신
//                _books.value = books.also { Log.d(TAG, "SearchResultViewModel: getBooks() - 가져온 _books 값 : ${it}") }
//            }
//            _isLoading.value = false //로딩 끝
//        }
//    }

}