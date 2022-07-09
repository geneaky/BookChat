package com.example.bookchat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookchat.R
import com.example.bookchat.adapter.SearchResultBookAdapter
import com.example.bookchat.databinding.ActivitySearchResultBinding
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.utils.SearchOptionType
import com.example.bookchat.utils.SharedPreferenceManager
import com.example.bookchat.viewmodel.OptionDrawerViewModel
import com.example.bookchat.viewmodel.SearchResultViewModel

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchResultBinding
    private lateinit var bookResultAdapter : SearchResultBookAdapter

    private lateinit var searchResultViewModel : SearchResultViewModel

    //JetPack ViewModel은 View와 1:1관계로 연결되기 때문에 한개의 View에서 다수개의 View를 사용할 수 없다.
    private lateinit var optionDrawerViewModel : OptionDrawerViewModel
//    private val searchResultViewModel : SearchResultViewModel by viewModels {
//        object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return SearchResultViewModel() as T
//            }
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SearchResultActivity: onCreate() - called")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_result)

        with(binding){
            lifecycleOwner = this@SearchResultActivity
            activity = this@SearchResultActivity
            searchResultViewModel = SearchResultViewModel()
            viewModel = searchResultViewModel
//            optionDrawerViewModel = OptionDrawerViewModel()

            searchTextEt.setText(intent.getStringExtra("SearchKeyWord") ?: "NO DATA")

            initAdapter()

            bookSearchResultRcyView.setHasFixedSize(true)
            bookSearchResultRcyView.layoutManager = GridLayoutManager(this@SearchResultActivity,2)

            //결과 액티비티 켜지자 마자 화면을 로딩시키고 데이터를 다 가져왔다면 리사이클러뷰 다시 갱신
            viewModel?.getBooks(
                intent.getStringExtra("SearchKeyWord")!!,
                intent.getSerializableExtra("OptionType") as SearchOptionType,
                success = {
                    bookResultAdapter.notifyDataSetChanged()
                }
            )

            //viewModel?.getBooks("조슈아 블로크",SearchOptionType.AUTHOR)
            //viewModel?.getBooks("9791165920760", SearchOptionType.ISBN)  //텅 빈값 넘어옴

            //검색창 엔터이벤트 등록
            searchTextEt.setOnEditorActionListener { textView, actionId, keyEvent ->
                Log.d(TAG, "SearchResultActivity: onCreate() - Enter!!")
                if(searchTextEt.text.toString() != ""){
                    SharedPreferenceManager.setSearchHistory(searchTextEt.text.toString()) // 검색어 저장

                    viewModel?.getBooks(
                        searchTextEt.text.toString(),
                        viewModel?.optionType?.value!!,
                        success = {
                            bookResultAdapter.notifyDataSetChanged()
                        }
                    )

                }else{
                    Toast.makeText(this@SearchResultActivity,"검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                false
            }

        }

    }

    private fun initAdapter() {
        bookResultAdapter = SearchResultBookAdapter(searchResultViewModel)

        //아이템 클릭 리스너 정의 (화면 전환이 필요하기 때문에 여기서 정의) (파라미터로 넘겨주던 람다로 하던 상관없음)
        //사실 그냥 context넘겨서 저기서 작업해도 상관없긴해
        bookResultAdapter.setItemClickListener(object: SearchResultBookAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val intent = Intent(this@SearchResultActivity, BookClickPageActivity::class.java)
                intent.putExtra("clickedBook",searchResultViewModel.books.value?.get(position) ?: "NO DATA")
                startActivity(intent)
            }
        })
        binding.bookSearchResultRcyView.adapter = bookResultAdapter
    }

    fun clearSearchText(){
        binding.searchTextEt.setText("")
    }

    fun clickCancleBtn(){
        finish()
    }


}