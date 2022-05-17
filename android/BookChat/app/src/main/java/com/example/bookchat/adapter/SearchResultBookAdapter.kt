package com.example.bookchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookchat.R
import com.example.bookchat.databinding.ItemBookSearchResultBinding

class SearchResultBookAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemBookSearchResultBinding: ItemBookSearchResultBinding
    private lateinit var itemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        itemBookSearchResultBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_book_search_result,
            parent,
            false
        )
        return BookResultViewHolder(itemBookSearchResultBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchResultBookAdapter.BookResultViewHolder) {

            // (1) 리스트 내 항목 클릭 시 onClick() 호출
            holder.binding.itemLayout.setOnClickListener {
                itemClickListener.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class BookResultViewHolder(val binding: ItemBookSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}