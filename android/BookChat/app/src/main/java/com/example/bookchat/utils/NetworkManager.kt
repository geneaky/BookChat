package com.example.bookchat.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.bookchat.App
/*
1. Android 기기의 ConnectivityManager 객체를 가져온다.
2. ConnectivityManager 객체를 사용하여 앱의 기본 네트워크 정보를 가지고 있는 Network 객체를 가져온다.
3. 1과 2에서 얻은 2개의 객체를 사용하여 NetworkCapabilities 객체를 가져온다.
4. NetworkCapabilities 객체를 사용한다.*/

class NetworkManager {

    fun checkNetworkState() :Boolean {
        val connectivityManager =
            App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            
            /* NetworkCapabilities.hasTransport() =
            Cellular, WIFI , VPN , Bluetooth 등의 연결상태를 확인할 수 있는 함수 */

            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            //WIFI , 셀룰러망 둘다 연결안되어있다면 Network처리를 할 수 없음으로 false 리턴
            else -> false
        }
    }

}