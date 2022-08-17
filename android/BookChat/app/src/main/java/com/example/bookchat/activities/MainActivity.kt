package com.example.bookchat.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
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
import com.example.bookchat.utils.Constants.TOKEN_PATH
import com.example.bookchat.viewmodel.MainViewModel
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var chatRoomAdapter: MainChatRoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        with(binding){
            lifecycleOwner =this@MainActivity
            activity = this@MainActivity
            userModel = mainViewModel
        }
            getUserInfo()
            initRecyclerView()
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
        val dialog = AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_Dialog)
        dialog.setTitle("정말 로그아웃하시겠습니까?")
            .setPositiveButton("취소",object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                }
            })
            .setNeutralButton("로그아웃",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    deleteToken()
                    finish()
                }
            })
            .setCancelable(true) //백버튼으로 닫히게 설정
            .show()
    }
    fun deleteToken(){
        val token = File(TOKEN_PATH)
        if (token.exists()) token.delete()
    }
    private fun getUserInfo(){
        binding.profile.clipToOutline = true //프로필 라운딩
        binding.userModel?.activityInitialization()
    }
    private fun initRecyclerView(){
        with(binding){
            chatRoomAdapter = MainChatRoomAdapter()
            chatRoomRecyclerView.adapter = chatRoomAdapter
            chatRoomRecyclerView.setHasFixedSize(true)
            chatRoomRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(chatRoomRecyclerView)
        }
    }
}