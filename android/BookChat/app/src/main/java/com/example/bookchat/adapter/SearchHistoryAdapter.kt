package com.example.bookchat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookchat.R
import com.example.bookchat.databinding.ItemSearchHistoryBinding
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.utils.SharedPreferenceManager

class SearchHistoryAdapter(var searchHistoryList :ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var binding: ItemSearchHistoryBinding
    private lateinit var itemClickListener: OnItemClickListener
    
    init {
        Log.d(TAG, "SearchHistoryAdapter: () - called")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = DataBindingUtil
            .inflate(LayoutInflater.from(parent.context), R.layout.item_search_history,parent,false)
        return SearchHistoryViewHolder(binding) //뷰홀더 생성해서 리턴
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) { //뷰홀더 받음
        binding.searchHistoryTv.text = searchHistoryList.get(position) //텍스트 값 설정
        binding.searchHistoryTv.setOnClickListener{
            itemClickListener.onClick(it,position)
        }
        binding.deleteSearchHistoryBtn.setOnClickListener {
            searchHistoryList.removeAt(position)
            SharedPreferenceManager.overWriteHistory(searchHistoryList)
            this.notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return searchHistoryList.size
    }

    inner class SearchHistoryViewHolder(val binding: ItemSearchHistoryBinding): RecyclerView.ViewHolder(binding.root){
    }

    // 아이템 클릭 리스너 인터페이스 정의
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    // 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}
