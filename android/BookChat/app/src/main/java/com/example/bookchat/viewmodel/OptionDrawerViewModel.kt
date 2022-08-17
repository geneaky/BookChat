package com.example.bookchat.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookchat.R
import com.example.bookchat.utils.Constants.TAG

//삭제 예정
class OptionDrawerViewModel :ViewModel() {
    private val _optionClickDataArray = MutableLiveData<Array<Int>>(Array(4,{0}))

    val optionClickDataArray : LiveData<Array<Int>>
        get() = _optionClickDataArray
    
    fun clickBtn(view: View){
        //배열 초기화
        _optionClickDataArray.value?.forEachIndexed { index, i ->
            _optionClickDataArray.value?.set(index,0)
        }
        when(view.id){
            R.id.bookNameOption_btn -> {
                _optionClickDataArray.value?.set(0, 1)
                Log.d(TAG, "OptionDrawerViewModel: 1 BtnClick() - _optionClickDataArray : ${_optionClickDataArray.value.contentToString()}")
            }
            R.id.authorNameOption_btn -> {
                _optionClickDataArray.value?.set(1, 1)
                Log.d(TAG, "OptionDrawerViewModel: 2 BtnClick() - _optionClickDataArray : ${_optionClickDataArray.value.contentToString()}")
            }
            R.id.isbnOption_btn ->{
                _optionClickDataArray.value?.set(2, 1)
                Log.d(TAG, "OptionDrawerViewModel: 3 BtnClick() - _optionClickDataArray : ${_optionClickDataArray.value.contentToString()}")
            }
            R.id.chatRoomNameOption_btn ->{
                _optionClickDataArray.value?.set(3, 1)
                Log.d(TAG, "OptionDrawerViewModel: 4 BtnClick() - _optionClickDataArray : ${_optionClickDataArray.value.contentToString()}")
            }
        }
    }
    
    val OPTIONCOLOR_BLACK = "#12121D"
    val OPTIONCOLOR_GRAY = "#BABABC"
}
