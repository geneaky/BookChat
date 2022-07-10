package com.example.bookchat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
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

}