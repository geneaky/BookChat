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

    //통신시에 항상 토큰값 실어서 보내게 인터셉터 등록해두기
    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain)
                : Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", SharedPreferenceManager.getToken()!!)
                .build()
            proceed(newRequest)
        }
    }


}