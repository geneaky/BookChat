package com.example.bookchat.Pagingtest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookchat.databinding.LoadStateBinding

class PagingLoadStateAdapter(private val retry: () -> Unit)
    : LoadStateAdapter<PagingLoadStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(
        holder: PagingLoadStateAdapter.LoadStateViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PagingLoadStateAdapter.LoadStateViewHolder {
        val binding = LoadStateBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LoadStateViewHolder(binding)
    }

    inner class LoadStateViewHolder(private val binding: LoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState : LoadState){
            with(binding){
                retryButton.setOnClickListener { retry() }
                isLoading = loadState is LoadState.Loading
                isError = loadState is LoadState.Error
                errorMessage = (loadState as? LoadState.Error)?.error?.message ?: "데이터 로드 실패(Default)"
            }
        }
    }

}