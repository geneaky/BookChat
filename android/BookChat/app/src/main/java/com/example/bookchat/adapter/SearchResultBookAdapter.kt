package com.example.bookchat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookchat.R
import com.example.bookchat.data.Book
import com.example.bookchat.databinding.ItemBookSearchResultBinding

class SearchResultBookAdapter
    : PagingDataAdapter<Book, SearchResultBookAdapter.BookResultViewHolder>(IMAGE_COMPARATOR) {
    private lateinit var itemBookSearchResultBinding: ItemBookSearchResultBinding
    private lateinit var itemClickListener: OnItemClickListener

    inner class BookResultViewHolder(val binding: ItemBookSearchResultBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(book : Book){
            binding.book = book
            binding.root.setOnClickListener {
                itemClickListener.onItemClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookResultViewHolder {
        itemBookSearchResultBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_book_search_result,
            parent,
            false
        )
        return BookResultViewHolder(itemBookSearchResultBinding)
    }

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

    interface OnItemClickListener {
        fun onItemClick(book :Book)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}