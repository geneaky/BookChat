package com.example.bookchat.data

import com.google.gson.annotations.SerializedName

data class Meta (
    @SerializedName("is_end")
    val isEnd :Boolean,
    @SerializedName("pageable_count")
    val pageableCount :Int,
    @SerializedName("total_count")
    val totalCount :Int
)