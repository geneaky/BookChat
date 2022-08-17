package com.example.bookchat.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.bookchat.App
class NetworkManager {
    fun checkNetworkState() :Boolean {
        val connectivityManager =
            App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}