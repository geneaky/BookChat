package com.example.bookchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.bookchat.databinding.ActivityLoginBinding
import com.example.bookchat.utils.Constansts.GOOGLE_LOGIN
import com.example.bookchat.utils.Constansts.KAKAO_LOGIN
import com.example.bookchat.utils.Constansts.TAG
import com.example.bookchat.utils.LoginType

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "LoginActivity: onCreate() - called")

        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.activity = this

    }
    fun openWeb(loginType : LoginType){
        Log.d(TAG, "LoginActivity: openWeb() - called")
        when(loginType){
            LoginType.KAKAO -> {
                Log.d(TAG, "LoginActivity: openWeb_KAKAO() - called")
                //WebViewActivity로 이동
                val intent = Intent(this,LoginWebActivity::class.java)
                intent.putExtra("URI",KAKAO_LOGIN)
                startActivity(intent)
            }
            LoginType.GOOGLE ->{
                Log.d(TAG, "LoginActivity: openWeb_GOOGLE() - called")
                //WebViewActivity로 이동
                val intent = Intent(this,LoginWebActivity::class.java)
                intent.putExtra("URI",GOOGLE_LOGIN)
                startActivity(intent)
            }
        }
    }

}