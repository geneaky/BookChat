package com.example.bookchat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookchat.R
import com.example.bookchat.data.Book
import com.example.bookchat.databinding.ItemBookSearchResultBinding
import com.example.bookchat.utils.Constants.TAG

class SearchResultBookAdapter
    : PagingDataAdapter<Book, SearchResultBookAdapter.BookResultViewHolder>(IMAGE_COMPARATOR) {

    private lateinit var itemBookSearchResultBinding: ItemBookSearchResultBinding
    private lateinit var itemClickListener: OnItemClickListener

    inner class BookResultViewHolder(val binding: ItemBookSearchResultBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(book : Book){
            Log.d(TAG, "BookResultViewHolder: bind() - ${book.title} 바인드됨")
            binding.book = book
            // 아이템 클릭시 어댑터에 지정해준 ClickListener 실행
            binding.root.setOnClickListener {
                itemClickListener.onItemClick(book)
            }
        }
    }

    //뷰홀더 객체 생성하는 메소드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookResultViewHolder {
        itemBookSearchResultBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_book_search_result,
            parent,
            false
        )
        return BookResultViewHolder(itemBookSearchResultBinding)
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시
    override fun onBindViewHolder(holder: BookResultViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    //Diffutil 구현
    companion object {
        private val IMAGE_COMPARATOR = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book) =
                oldItem.isbn == newItem.isbn

            override fun areContentsTheSame(oldItem: Book, newItem: Book) =
                oldItem == newItem
        }
    }

    // 내부 아이템 클릭 리스너 인터페이스 정의
    interface OnItemClickListener {
        fun onItemClick(book :Book)
    }

    // 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    //삭제 보류 //////////////////////////////////////
//    fun addItems(items: List<Book>) {
//        this.items.addAll(items)
//        notifyDataSetChanged()
//    }
//
//    fun clear() {
//        this.items.clear()
//        notifyDataSetChanged()
//    }

}