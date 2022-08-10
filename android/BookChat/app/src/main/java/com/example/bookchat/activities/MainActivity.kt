package com.example.bookchat.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.bookchat.R
import com.example.bookchat.adapter.MainChatRoomAdapter
import com.example.bookchat.databinding.ActivityMainBinding
import com.example.bookchat.utils.ActivityType
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.viewmodel.MainViewModel
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var chatRoomAdapter: MainChatRoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity: onCreate() - called")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        with(binding){
            lifecycleOwner =this@MainActivity
            activity = this@MainActivity
            userModel = mainViewModel

            //유저 정보 불러오기
            userModel?.activityInitialization()

            //프로필 이미지 라운드 설정
            binding.profile.clipToOutline = true

            chatRoomAdapter = MainChatRoomAdapter()
            chatRoomRecyclerView.adapter = chatRoomAdapter
            chatRoomRecyclerView.setHasFixedSize(true)
            chatRoomRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(chatRoomRecyclerView)
        }
    }
    fun clickMenu() {
        with(binding){
            if(drawerlayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerlayout.closeDrawer(Gravity.RIGHT)
                return
            }
            drawerlayout.openDrawer(Gravity.RIGHT)
        }
    }
    fun changePage(activityType: ActivityType) {
        val targetActivity = when(activityType) {
            ActivityType.bookShelfActivity -> { BookShelfActivity::class.java }
            ActivityType.searchActivity -> { SearchActivity::class.java }
        }
        val intent = Intent(this, targetActivity)
        startActivity(intent)
    }
    fun clickSignOut(){
        //다이얼로그 디자인 수정할 것
        //다이얼 로그로 정말 로그아웃 하시겠습니까 물어보기
        val dialog = AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_Dialog)
        dialog.setTitle("정말 로그아웃하시겠습니까?")
            .setPositiveButton("취소",object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    Log.d(TAG, "MainActivity: dialog- onClick() 취소- called")
                }
            })
            .setNeutralButton("로그아웃",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    Log.d(TAG, "MainActivity: dialog- onClick() 로그아웃- called")
                    deleteToken()
                    finish()
                }
            })
            .setCancelable(true) //백버튼으로 닫히게 설정
            .show()
    }
    fun deleteToken(){
        try {
            val token = File("/data/data/com.example.bookchat/shared_prefs/encryptedShared.xml")
            if (token.exists()) token.delete()
        }catch (e :Exception){
            e.printStackTrace()
        }
    }
}