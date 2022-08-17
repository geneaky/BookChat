package com.example.bookchat.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookchat.App
import com.example.bookchat.Paging.BookSearchResultPagingLoadStateAdapter
import com.example.bookchat.viewmodel.BookSearchResultViewModelFactory
import com.example.bookchat.R
import com.example.bookchat.adapter.SearchResultBookAdapter
import com.example.bookchat.data.Book
import com.example.bookchat.data.BookSearchOption
import com.example.bookchat.databinding.ActivitySearchResultBinding
import com.example.bookchat.repository.BookRepository
import com.example.bookchat.viewmodel.SearchResultViewModel

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchResultBinding
    private lateinit var bookResultAdapter : SearchResultBookAdapter
    private lateinit var searchResultViewModel : SearchResultViewModel
    private lateinit var searchText : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_result)
        searchText = intent.getStringExtra("SearchKeyWord") ?: "NO DATA"

        with(binding){
            lifecycleOwner = this@SearchResultActivity
            activity = this@SearchResultActivity
            searchTextEt.setText(searchText)
        }
        initAdapter()
        addLoadStateListener()
        initViewModel()
        initRcyView(binding)
        initSearchWindow(binding)
        initPagingDataObserver()
        search(searchText)
    }

    private fun initAdapter() {
        bookResultAdapter = SearchResultBookAdapter()
        //아이템 클릭 리스너 정의 (화면 전환이 필요하기 때문에 여기서 정의)
        bookResultAdapter.setItemClickListener(object: SearchResultBookAdapter.OnItemClickListener{
            override fun onItemClick(book : Book) {
                val intent = Intent(this@SearchResultActivity, BookClickPageActivity::class.java)
                intent.putExtra("clickedBook",book)
                startActivity(intent)
            }
        })
    }

    private fun initViewModel(){
        val repository = BookRepository()
        val viewModelFactory = BookSearchResultViewModelFactory(repository)
        searchResultViewModel = ViewModelProvider(this@SearchResultActivity,viewModelFactory).get(SearchResultViewModel::class.java)
    }

    private fun initRcyView(binding : ActivitySearchResultBinding){
        with(binding){
            bookSearchResultRcyView.setHasFixedSize(true) //사이즈 고정
            bookSearchResultRcyView.layoutManager = GridLayoutManager(this@SearchResultActivity,2)
            bookSearchResultRcyView.adapter = bookResultAdapter.withLoadStateHeaderAndFooter(
                header = BookSearchResultPagingLoadStateAdapter { bookResultAdapter.retry() },
                footer = BookSearchResultPagingLoadStateAdapter { bookResultAdapter.retry() }
            )
        }
    }

    private fun initSearchWindow(binding : ActivitySearchResultBinding){
        with(binding){
            //검색창 엔터이벤트 등록
            searchTextEt.setOnEditorActionListener { _, _, _ ->
                search(searchTextEt.text.toString())
                false
            }
        }
    }

    private fun initPagingDataObserver(){
        searchResultViewModel.pagingData.observe(this@SearchResultActivity, Observer {
            bookResultAdapter.submitData(this@SearchResultActivity.lifecycle, it)
        })
    }

    //로드 상태에 따른 UI작업
    private fun addLoadStateListener(){
        bookResultAdapter.addLoadStateListener { combinedLoadStates ->
            /*
                CombinedLoadStates.refresh: PagingData를 처음 로드할 때의 로드 상태를 나타냄
                CombinedLoadStates.prepend: 목록의 시작 부분에서 데이터를 로드하는 작업의 로드 상태를 나타냄
                CombinedLoadStates.append: 목록의 끝에서 데이터를 로드하는 작업의 로드 상태를 나타냄
                => 출력
                        LoadState.NotLoading : 활성 로드 작업이 없고 오류가 없음
                        LoadState.Loading : 활성 로드 작업이 있음
                        LoadState.Error : 오류가 있음
            */
            binding.apply {
                val appendLoadState = combinedLoadStates.source.append //끄트머리에서 데이터 로드 상태
                val refreshLoadState = combinedLoadStates.source.refresh //새로고침 데이터 로드 상태
                totalCount = searchResultViewModel.getResultCount().toString()
                // 로딩 중 일 때
                loadingProgressBar.isVisible = refreshLoadState is LoadState.Loading

                // 로딩 중이지 않을 때 (활성 로드 작업이 없고 에러가 없음)
                bookSearchResultRcyView.isVisible = refreshLoadState is LoadState.NotLoading

                // 로딩 에러 발생 시
                retryButton.isVisible = refreshLoadState is LoadState.Error
                errorText.isVisible = refreshLoadState is LoadState.Error

                // 활성 로드 작업이 없고 에러가 없음 & 로드할 수 없음 & 개수 1 미만 (empty)
                if (refreshLoadState is LoadState.NotLoading
                    && appendLoadState.endOfPaginationReached
                    && bookResultAdapter.itemCount < 1
                ) {
                    bookSearchResultRcyView.isVisible = false
                    emptyResultImg.isVisible = true
                    emptyResultText.isVisible = true
                } else {
                    emptyResultImg.isVisible = false
                    emptyResultText.isVisible = false
                }

            }
        }
    }
    private fun search(searchText :String){
        if(!App.instance.isNetworkConnected()) {
            Toast.makeText(App.instance.applicationContext,"네트워크가 연결되어 있지 않습니다.\n네트워크를 연결해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        //넘겨받은 검색어로 검색
        val bookSearchOption = BookSearchOption(
            keyWord = searchText,
            sort = "LATEST"
        )
        if(bookSearchOption.keyWord.isNotEmpty()){
            //페이징 시작
            //(ViewModel의 pagingData가 값이 변경됨(PagingSource를 통해 지정된 Size만큼 아이템을 PagingData<Book>형식으로 받아옴) ->
            //submitData()로 PagingData<Book>를 adapter에 제출함 -> ViewHolder에 바인딩 됨 -> 불러온 데이터가 다 소진되기 전에 미리 미리 위의 과정을 반복)
            searchResultViewModel.searchBook(bookSearchOption) 
        }else{
            Toast.makeText(this@SearchResultActivity,"검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    fun clearSearchText(){
        binding.searchTextEt.setText("")
    }

    fun clickCancleBtn(){
        finish()
    }
    fun clickRetryBtn(){
        search(searchText)
    }
}