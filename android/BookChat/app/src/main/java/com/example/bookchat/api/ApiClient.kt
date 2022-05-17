package com.example.bookchat.api

import com.example.bookchat.utils.Constants.DOMAIN
import com.example.bookchat.utils.SharedPreferenceManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ApiClient {

    fun getApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DOMAIN)
            .client(provideOkHttpClient(AppInterceptor())) //OkHttp 클라이언트 주입
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun provideOkHttpClient(
        interceptor: AppInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain)
                : Response = with(chain) {
            //SharedPreferenceManager.getToken()!! 오류 뜰 수도 있으니 나중에 수정할 것
            val newRequest = request().newBuilder()
                .addHeader("Authorization", SharedPreferenceManager.getToken()!!)
                .build()

            proceed(newRequest)
        }
    }


}