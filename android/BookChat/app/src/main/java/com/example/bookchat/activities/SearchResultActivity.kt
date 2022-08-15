package com.example.bookchat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.viewmodel.SearchResultViewModel

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchResultBinding
    private lateinit var bookResultAdapter : SearchResultBookAdapter
    private lateinit var searchResultViewModel : SearchResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SearchResultActivity: onCreate() - called")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_result)
        val searchText = intent.getStringExtra("SearchKeyWord") ?: "NO DATA"

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

            //viewModel?.getBooks("조슈아 블로크",SearchOptionType.AUTHOR)
            //viewModel?.getBooks("9791165920760", SearchOptionType.ISBN)  //텅 빈값 넘어옴

            //검색어 , 검색 옵션 , 성공시 콜백, 실패시 콜백 전달.
            //성공시 값이 없습니다 이미지 , 텍스트 INVISIBLE 처리 / 리사이클러뷰, 배경 VISIBLE 처리 후 리사이클러뷰 갱신
            //실패시 값이 없습니다 이미지 , 텍스트 VISIBLE 처리 / 리사이클러뷰 ,배경 INVISIBLE 처리
            //결과 액티비티 켜지자 마자 화면을 로딩시키고 데이터를 다 가져왔다면 리사이클러뷰 다시 갱신
    }

    private fun initAdapter() {
        Log.d(TAG, "SearchResultActivity: initAdapter() - called")
        bookResultAdapter = SearchResultBookAdapter()
        //아이템 클릭 리스너 정의 (화면 전환이 필요하기 때문에 여기서 정의)
        bookResultAdapter.setItemClickListener(object: SearchResultBookAdapter.OnItemClickListener{
            override fun onItemClick(book : Book) {
                val intent = Intent(this@SearchResultActivity, BookClickPageActivity::class.java)
                intent.putExtra("clickedBook",book)
                startActivity(intent)
            }
        })
        binding.bookSearchResultRcyView.adapter = bookResultAdapter
    }

    private fun initViewModel(){
        Log.d(TAG, "SearchResultActivity: initViewModel() - called")
        val repository = BookRepository()
        val viewModelFactory = BookSearchResultViewModelFactory(repository)
        searchResultViewModel = ViewModelProvider(this@SearchResultActivity,viewModelFactory).get(SearchResultViewModel::class.java)
    }

    private fun initRcyView(binding : ActivitySearchResultBinding){
        Log.d(TAG, "SearchResultActivity: initRcyView() - called")
        with(binding){
            bookSearchResultRcyView.setHasFixedSize(true) //사이즈 고정
            bookSearchResultRcyView.layoutManager = GridLayoutManager(this@SearchResultActivity,2)
            // header, footer 설정 (끝부분에 다다랐을 떄 값 요청함 retry()는 에러시 새로고침하라고 전달함)
            bookResultAdapter.withLoadStateHeaderAndFooter(
                header = BookSearchResultPagingLoadStateAdapter { bookResultAdapter.retry() },
                footer = BookSearchResultPagingLoadStateAdapter { bookResultAdapter.retry() }
            )
        }
    }

    private fun initSearchWindow(binding : ActivitySearchResultBinding){
        Log.d(TAG, "SearchResultActivity: initSearchWindow() - called")
        with(binding){
            //검색창 엔터이벤트 등록
            searchTextEt.setOnEditorActionListener { _, _, _ ->
                Log.d(TAG, "SearchResultActivity: onCreate() - Enter!!")
                search(searchTextEt.text.toString())
                false
            }
        }
    }

    private fun initPagingDataObserver(){
        Log.d(TAG, "SearchResultActivity: initPagingDataObserver() - called")
        //pagingData에 Observer(콜백)메소드 등록 (submitData 메소드로 PagingData<Post>를 PagingAdapter로 넘겨줌) (Data를 Update)
        // (그 이후, Paging 이벤트들은 PagingDataDiffer에서 수집되어서 페이징 상태를 관리해줌)
        searchResultViewModel.pagingData.observe(this@SearchResultActivity, Observer {
            bookResultAdapter.submitData(this@SearchResultActivity.lifecycle, it)
            Log.d(TAG, "SearchResultActivity: onCreate() - submitData 작동 시작")
        })
    }

    //로드 상태에 따른 UI작업
    private fun addLoadStateListener(){
        Log.d(TAG, "SearchResultActivity: addLoadStateListener() - called")
        // 로딩 상태 리스너
        bookResultAdapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                Log.d(TAG, "SearchResultActivity: addLoadStateListener() - combinedLoadStates.source.append  : ${combinedLoadStates.source.append}")
                val loadState = combinedLoadStates.source.refresh
                Log.d(TAG, "SearchResultActivity: addLoadStateListener() - loadState : ${loadState}")
                // 로딩 중 일 때
                loadingProgressBar.isVisible = loadState is LoadState.Loading

                // 로딩 중이지 않을 때 (활성 로드 작업이 없고 에러가 없음)
                bookSearchResultRcyView.isVisible = loadState is LoadState.NotLoading

                // 로딩 에러 발생 시
                retryButton.isVisible = loadState is LoadState.Error
                errorText.isVisible = loadState is LoadState.Error

                // 활성 로드 작업이 없고 에러가 없음 & 로드할 수 없음 & 개수 1 미만 (empty)
                if (loadState is LoadState.NotLoading
                    && combinedLoadStates.append.endOfPaginationReached
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
            title = searchText,
            sort = "LATEST"
        )
        if(bookSearchOption.title.isNotEmpty()){
            Log.d(TAG, "SearchResultActivity: search() - 페이징 시작")
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
        if(!App.instance.isNetworkConnected()) {
            Toast.makeText(App.instance.applicationContext,"네트워크가 연결되어 있지 않습니다.\n네트워크를 연결해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        bookResultAdapter.refresh()
    }
}