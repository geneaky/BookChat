package com.example.bookchat.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.bookchat.R
import com.example.bookchat.databinding.ActivityLoginBinding
import com.example.bookchat.utils.Constants.GOOGLE_LOGIN
import com.example.bookchat.utils.Constants.KAKAO_LOGIN
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.utils.LoginType
import com.example.bookchat.utils.SharedPreferenceManager

class LoginActivity : AppCompatActivity() {
    //토큰있으면 자동 로그인 없으면 다시 웹으로 토큰 받아오는거 구현
    //리프레시 토큰 교체하는거 구현해야함
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        with(binding){
            lifecycleOwner = this@LoginActivity
            activity = this@LoginActivity
        }
        handleIntent(intent)
        checkToken()
    }

    private fun handleIntent(intent: Intent) {
        val FinalUri: Uri? = intent.data
        val token = FinalUri?.getQueryParameter("token")
        if (token.isNullOrEmpty()){ return }
        SharedPreferenceManager.saveToken(token)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun checkToken() {
        if(SharedPreferenceManager.isTokenEmpty()){
            Toast.makeText(this,"로그인 정보가 없습니다.\n로그인을 진행해주세요.",Toast.LENGTH_LONG).show()
            return
        }
        //불러온 토큰의 기간이 만료된거라면 새로운 리프레시토큰을 요청해야함
        Toast.makeText(this,"로그인 정보를 불러옵니다.",Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    //현재 액티비티가 살아있고, 재호출되는 경우 onCreate()대신 호출됨
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    fun openWeb(loginType : LoginType){
        val uri = when(loginType){
            LoginType.KAKAO ->{ KAKAO_LOGIN }
            LoginType.GOOGLE ->{ GOOGLE_LOGIN }
        }
        if(sdkVersionCheck()){ //브라우저로 진행
            val intent = Intent(Intent.ACTION_VIEW,Uri.parse(uri))
            startActivity(intent)
            finish()
            return
        }
        //크롬 커스텀 탭으로 진행
        openCustomTab(uri)
    }

    private fun openCustomTab(uri :String){
        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ContextCompat.getColor(this, R.color.black))
        CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(params.build())
            .setShowTitle(false)
            .setInstantAppsEnabled(true)
            .build()
            .launchUrl(this, Uri.parse(uri))
    }

    private fun sdkVersionCheck() :Boolean{
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.S ||
                Build.VERSION.SDK_INT == Build.VERSION_CODES.S_V2
    }
}