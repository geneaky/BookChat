package com.example.bookchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookchat.R
import com.example.bookchat.databinding.ItemSearchHistoryBinding
import com.example.bookchat.utils.SharedPreferenceManager

class SearchHistoryAdapter(var searchHistoryList :ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var binding: ItemSearchHistoryBinding
    private lateinit var itemClickListener: OnItemClickListener
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = DataBindingUtil
            .inflate(LayoutInflater.from(parent.context), R.layout.item_search_history,parent,false)
        return SearchHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        binding.searchHistoryTv.text = searchHistoryList.get(position)
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

    inner class SearchHistoryViewHolder(val binding: ItemSearchHistoryBinding): RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}
