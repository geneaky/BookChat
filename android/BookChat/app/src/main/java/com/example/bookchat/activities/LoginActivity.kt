package com.example.bookchat.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bookchat.R
import com.example.bookchat.databinding.ActivityLoginBinding
import com.example.bookchat.utils.Constants.GOOGLE_LOGIN
import com.example.bookchat.utils.Constants.KAKAO_LOGIN
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.utils.LoginType

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "LoginActivity: onCreate() - called")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.activity = this
    }
    fun openWeb(loginType : LoginType){
        Log.d(TAG, "LoginActivity: openWeb() - called")

        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.S ||
            Build.VERSION.SDK_INT == Build.VERSION_CODES.S_V2){

                when(loginType){

                    LoginType.KAKAO ->{
                        val intent = Intent(Intent.ACTION_VIEW,Uri.parse(KAKAO_LOGIN))
                        startActivity(intent)
                    }

                    LoginType.GOOGLE ->{
                        val intent = Intent(Intent.ACTION_VIEW,Uri.parse(GOOGLE_LOGIN))
                        startActivity(intent)
                    }

                }

        }else{

            when(loginType){

                LoginType.KAKAO -> {
                    Log.d(TAG, "LoginActivity: openWeb_KAKAO() - called")
                    //WebViewActivity로 이동
                    val intent = Intent(this, LoginWebActivity::class.java)
                    intent.putExtra("URI",KAKAO_LOGIN)
                    startActivity(intent)
                }

                LoginType.GOOGLE ->{
                    Log.d(TAG, "LoginActivity: openWeb_GOOGLE() - called")
                    //WebViewActivity로 이동
                    val intent = Intent(this, LoginWebActivity::class.java)
                    intent.putExtra("URI",GOOGLE_LOGIN)
                    startActivity(intent)
                }

            }

        }

    }

}