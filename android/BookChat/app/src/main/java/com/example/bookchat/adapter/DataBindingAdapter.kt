package com.example.bookchat.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookchat.R
import com.example.bookchat.data.Book

object DataBindingAdapter {
    @JvmStatic
    @BindingAdapter("loadUrl")
    fun loadUrl(imageView: ImageView, url: String?){
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.default_img)
            .error(R.drawable.default_img)
            .into(imageView)
    }
}