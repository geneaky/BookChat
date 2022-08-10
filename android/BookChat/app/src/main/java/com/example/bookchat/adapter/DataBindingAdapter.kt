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

//    @JvmStatic //Object밖에 바로 fun으로 정의할 거 아니면 써줘야함
//    @BindingAdapter("setItem")
//    fun RecyclerView.setAdapterItems(items: ArrayList<Book>?){
//        with((adapter as SearchResultBookAdapter)){
//            clear()
//            items?.let { addItems(it) }
//        }
//    }
}