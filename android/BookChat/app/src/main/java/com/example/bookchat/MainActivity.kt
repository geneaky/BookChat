package com.example.bookchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.bookchat.adapter.MainChatRoomAdapter
import com.example.bookchat.databinding.ActivityMainBinding
import com.example.bookchat.utils.Constansts.TAG
import com.example.bookchat.viewmodel.UserInforViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var userInforViewModel: UserInforViewModel
    private lateinit var chatRoomAdapter: MainChatRoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        userInforViewModel = ViewModelProvider(this).get(UserInforViewModel::class.java)

        with(binding){
            lifecycleOwner =this@MainActivity
            model = userInforViewModel
            activity = this@MainActivity

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
    fun open_closeMenu() {
        with(binding){
            if(drawerlayout.isDrawerOpen(Gravity.RIGHT)) {
                Log.d(TAG, "MainActivity: closeMenu() - called")
                drawerlayout.closeDrawer(Gravity.RIGHT)
            } else {
                Log.d(TAG, "MainActivity: openMenu() - called")
                drawerlayout.openDrawer(Gravity.RIGHT)
            }
        }
    }
    fun changePage(pageName: String) {
        when(pageName){
            "BookShelfActivity" -> {
                val intent = Intent(this,BookShelfActivity::class.java)
                startActivity(intent)
            }
            "SearchActivity" -> {
                val intent = Intent(this,SearchActivity::class.java)
                startActivity(intent)
            }
        }
    }

}