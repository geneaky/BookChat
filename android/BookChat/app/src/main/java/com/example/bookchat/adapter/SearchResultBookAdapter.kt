package com.example.bookchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookchat.R
import com.example.bookchat.data.Book
import com.example.bookchat.databinding.ItemBookSearchResultBinding
import com.example.bookchat.viewmodel.SearchResultViewModel

class SearchResultBookAdapter(val searchResultViewModel :SearchResultViewModel)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemBookSearchResultBinding: ItemBookSearchResultBinding
    private lateinit var itemClickListener: OnItemClickListener
    private val items = ArrayList<Book>()

    //뷰홀더 객체 생성하는 메소드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        itemBookSearchResultBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_book_search_result,
            parent,
            false
        )
        return BookResultViewHolder(itemBookSearchResultBinding)
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchResultBookAdapter.BookResultViewHolder) {

            holder.binding.book = searchResultViewModel.books.value?.get(position)

            // 리스트 내 항목 클릭 시 OnItemClickListener 인터페이스 onClick() 호출
            holder.binding.itemLayout.setOnClickListener {
                itemClickListener.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return searchResultViewModel.books.value?.size ?: 0
    }

    inner class BookResultViewHolder(val binding: ItemBookSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    // 아이템 클릭 리스너 인터페이스 정의
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    // 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    fun addItems(items: List<Book>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }
}