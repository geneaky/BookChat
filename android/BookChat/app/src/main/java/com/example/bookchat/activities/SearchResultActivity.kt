package com.example.bookchat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bookchat.R
import com.example.bookchat.adapter.SearchResultBookAdapter
import com.example.bookchat.databinding.ActivitySearchResultBinding
import com.example.bookchat.utils.Constants.TAG

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchResultBinding
    private lateinit var bookResultAdapter : SearchResultBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SearchResultActivity: onCreate() - called")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_result)

        with(binding){
            lifecycleOwner = this@SearchResultActivity
            activity = this@SearchResultActivity

            searchTextEt.setText(intent.getStringExtra("SearchKeyWord") ?: "NO DATA")

            bookResultAdapter = SearchResultBookAdapter()
            bookResultAdapter.setItemClickListener(object: SearchResultBookAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    // 클릭 시 이벤트 작성
                    Toast.makeText(this@SearchResultActivity,"클릭발생",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SearchResultActivity, SearchResult_BookPageActivity::class.java)
                    startActivity(intent)
                }
            })

            bookSearchResultRcyView.adapter = bookResultAdapter
            bookSearchResultRcyView.setHasFixedSize(true)
            bookSearchResultRcyView.layoutManager = LinearLayoutManager(this@SearchResultActivity,
                RecyclerView.HORIZONTAL,false)
            val snapHelper1 = LinearSnapHelper()
            snapHelper1.attachToRecyclerView(bookSearchResultRcyView)
        }

    }

    fun clearSearchText(){
        binding.searchTextEt.setText("")
    }

    fun clickCancleBtn(){
        finish()
    }
}