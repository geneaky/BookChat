package com.example.bookchat

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.example.bookchat.utils.Constansts.TAG

class LoginWebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "LoginWebActivity: onCreate() - called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_web)

        val StartUri = intent.getStringExtra("URI")
        handleIntent(intent)

        StartUri?.let{
            //로딩화면도 구현할 것

            val builder = CustomTabsIntent.Builder()
            val params = CustomTabColorSchemeParams.Builder()
            params.setToolbarColor(ContextCompat.getColor(this, R.color.black))
            builder.setDefaultColorSchemeParams(params.build())

            builder.setShowTitle(false)

            builder.setInstantAppsEnabled(true)

            val customBuilder = builder.build()

            customBuilder.launchUrl(this, Uri.parse(StartUri))
        }

    }


    override fun onResume() {
        Log.d(TAG, "LoginWebActivity: onResume() - called")
        super.onResume()
        //크롬탭 꺼지면 Activity 종료
        //  = 크롬 커스텀 탭 꺼지고 Activity로 복귀할 때 바로 Activity 종료
        //finish()
    }

    //현재 액티비티가 살아있고, 재호출되는 경우 onCreate()대신 호출됨
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "LoginWebActivity: onNewIntent() - called")

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        Log.d(TAG, "LoginWebActivity: handleIntent() - called")

        val appLinkAction = intent.action
        val FinalUri: Uri? = intent.data
        val token = FinalUri?.getQueryParameter("token")
        Log.d(TAG, "LoginWebActivity: handleIntent() - token : $token")

        if (token != null){
            //SharedPreference에 토큰 저장 (암호화 필요)
            var pref = this@LoginWebActivity.getSharedPreferences("token", Context.MODE_PRIVATE)
            var editor = pref.edit()
            editor.putString("token","$token")
            editor.apply()
            Log.d(TAG, "LoginWebActivity: handleIntent() - checkPref = ${pref.getString("token","notToken")}")

            //토큰 받았으면 다음페이지로 넘어가는 거 구현해야함

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

        }else{
            //handle error
        }
    }
    
}
