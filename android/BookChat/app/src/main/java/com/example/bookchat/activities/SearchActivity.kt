package com.example.bookchat.activities

import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bookchat.R
import com.example.bookchat.adapter.SearchHistoryAdapter
import com.example.bookchat.databinding.ActivitySearchBinding
import com.example.bookchat.utils.Constants.TAG
import com.example.bookchat.utils.SearchOptionType
import com.example.bookchat.utils.SharedPreferenceManager
import com.example.bookchat.viewmodel.OptionDrawerViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class SearchActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchBinding
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private var windowClickCheck = 0
    private var optionType = SearchOptionType.TITLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.lifecycleOwner = this@SearchActivity
        binding.activity = this@SearchActivity
        binding.drawerViewModel = OptionDrawerViewModel() //의미 없음 라이프사이클 연결이 안되서 ,
        // 데이터 바인딩 써서하려면 프레그먼트나 액티비티로 만들어야하는데 그러면 SlidingUpPanddingWindow를 못씀 아마도 직접 구현해야할거임

        with(binding){
            //검색창 엔터이벤트 등록
            searchTextEt.setOnEditorActionListener { textView, actionId, keyEvent ->
                Log.d(TAG, "SearchActivity: onCreate() - Enter!!")
                if(searchTextEt.text.toString() != ""){
                    SharedPreferenceManager.setSearchHistory(searchTextEt.text.toString()) // 검색어 저장
                    openResult() //페이지 이동
                }else{
                    Toast.makeText(this@SearchActivity,"검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                false
            }

            //검색기록 RcyV설정
            searchHistoryAdapter = SearchHistoryAdapter(SharedPreferenceManager.getSearchHistory())
            with(SearchHistoryRycv){
                adapter = searchHistoryAdapter
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@SearchActivity,
                    RecyclerView.VERTICAL,false)
                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(SearchHistoryRycv)
            }


        }

        //옵션 슬라이드 설정
        binding.optionFrame.addPanelSlideListener( object : SlidingUpPanelLayout.PanelSlideListener{

            //패널이 슬라이드 중일 때
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
            }

            //패널의 상태가 변했을 때 (올라왔거나 내려갔거나)
            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    binding.optionFrame.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
                }else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED){
                    Toast.makeText(this@SearchActivity,"검색 필터를 설정해주세요.",Toast.LENGTH_SHORT).show()
                }
            }
        })
        //초기값 숨김 지정
        binding.optionFrame.panelState = SlidingUpPanelLayout.PanelState.HIDDEN

    }

    //검색창 클릭 이벤트(열기)
    fun clickSearchWindow(){
        Log.d(TAG, "SearchActivity: clickLayoutTest() - called")

        if (windowClickCheck == 0){

            windowClickCheck = 1
            //검색창 이동 애니메이션
            val openWindowAnimator = AnimatorInflater.loadAnimator( this,
                R.animator.move_search_window_open
            ).apply {
                interpolator = AccelerateDecelerateInterpolator()
                doOnStart { Log.d(TAG, "SearchActivity: clickSearchWindow() - AnimationStart") }
                doOnEnd { Log.d(TAG, "SearchActivity: clickSearchWindow() - AnimationEnd") }
                binding.SearchWindow.pivotX = 0.0f
                binding.SearchWindow.pivotY = 0.0f
                setTarget(binding.SearchWindow)
                start()
            }

            //옵션버튼 이동 애니메이션
            val openWindowAnimator2 = AnimatorInflater.loadAnimator( this,
                R.animator.move_search_option_btn_open
            ).apply {
                interpolator = AccelerateDecelerateInterpolator()
                doOnStart { Log.d(TAG, "SearchActivity: clickSearchWindow() - btn_AnimationStart") }
                doOnEnd { Log.d(TAG, "SearchActivity: clickSearchWindow() - btn_AnimationEnd") }
                setTarget(binding.searchOptionBtn)
                start()
            }

            //애니메이션 작동시 노출View 설정
            Handler(Looper.getMainLooper()).postDelayed({
                with(binding){
                    SearchText.visibility = INVISIBLE
                    searchTextTv.visibility = INVISIBLE
                    searchTextEt.visibility = VISIBLE

                    // 키보드 포커스 & 키보드 올리기
                    searchTextEt.requestFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(searchTextEt, SHOW_IMPLICIT)

                    searchWindowDeleteBtn.visibility = VISIBLE
                    searchWindowCencelBtn.visibility = VISIBLE
                    recentSearchTitle.visibility = VISIBLE

                    //검색기록 노출
                    SearchHistoryRycv.visibility = VISIBLE

                }}, 300L
            )

        }else{

        }

    }

    //검색창 클릭 이벤트(닫기)
    fun clickCancleBtn(){

        if(windowClickCheck == 1){
            windowClickCheck =0

            //검색창 이동 애니메이션
            val openWindowAnimator = AnimatorInflater.loadAnimator( this,
                R.animator.move_search_window_close
            ).apply {
                interpolator = AccelerateDecelerateInterpolator()
                doOnStart { Log.d(TAG, "SearchActivity: clickCancleBtn() - AnimationStart") }
                doOnEnd { Log.d(TAG, "SearchActivity: clickCancleBtn() - AnimationEnd") }
                binding.SearchWindow.pivotX = 0.0f
                binding.SearchWindow.pivotY = 0.0f
                setTarget(binding.SearchWindow)
                start()
            }

            //옵션버튼 이동 애니메이션
            val openWindowAnimator2 = AnimatorInflater.loadAnimator( this,
                R.animator.move_search_option_btn_close
            ).apply {
                interpolator = AccelerateDecelerateInterpolator()
                doOnStart { Log.d(TAG, "SearchActivity: clickCancleBtn() - btn_AnimationStart") }
                doOnEnd { Log.d(TAG, "SearchActivity: clickCancleBtn() - btn_AnimationEnd") }
                setTarget(binding.searchOptionBtn)
                start()
            }

            //애니메이션 작동시 노출View 설정
            Handler(Looper.getMainLooper()).postDelayed({
                with(binding){
                    SearchText.visibility = VISIBLE
                    searchTextTv.visibility = VISIBLE
                    searchTextEt.visibility = INVISIBLE
                    clearSearchText()

                    // 키보드 내리기
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchTextEt.windowToken, 0)

                    searchWindowDeleteBtn.visibility = INVISIBLE
                    searchWindowCencelBtn.visibility = INVISIBLE
                    recentSearchTitle.visibility = INVISIBLE

                    //검색기록 숨기기
                    SearchHistoryRycv.visibility = INVISIBLE

                }}, 100L
            )

        }else{

        }
    }

    //키보드가 아닌 다른곳 누르면 키보드 내림 (작동 확인 필요)
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "SearchActivity: onTouchEvent() - called")
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true
    }

    fun clearSearchText(){
        Log.d(TAG, "SearchActivity: clearSearchText() - called")
        binding.searchTextEt.setText("")
    }

    fun openResult(){
        Log.d(TAG, "SearchActivity: openResult() - called")
        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra("SearchKeyWord",binding.searchTextEt.text.toString())
        intent.putExtra("OptionType",optionType)
        startActivity(intent)
    }

    fun clearHistory(){
        SharedPreferenceManager.clearSearchHistory()
        //리사이클러뷰 갱신
        searchHistoryAdapter.searchHistoryList = SharedPreferenceManager.getSearchHistory()
        searchHistoryAdapter.notifyDataSetChanged()
    }

    fun clickOptionBtn(){
        Log.d(TAG, "SearchActivity: clickOptionBtn() - called")
        with(binding){
            when(optionFrame.panelState){
                SlidingUpPanelLayout.PanelState.HIDDEN -> {
                    optionFrame.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                }
                SlidingUpPanelLayout.PanelState.EXPANDED -> {
                    optionFrame.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                }
            }
        }
    }

    //여러 개의 View 클릭 이벤트 처리는 한 개의 View 클릭 처리와 함수 형태와 xml에 적는 형식이 다르다.
    //클릭한거만 검은색 클릭 안된애들은 회색으로 구현
    fun clizckOptionItem(view : View){

        when(view.id){
            R.id.bookNameOption_btn -> {
                binding.optionFrame.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
                optionType = SearchOptionType.TITLE
                changeColorElseOptionBtn(view)
            }
            R.id.authorNameOption_btn -> {
                binding.optionFrame.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
                optionType = SearchOptionType.AUTHOR
                changeColorElseOptionBtn(view)
            }
            R.id.isbnOption_btn ->{
                binding.optionFrame.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
                optionType = SearchOptionType.ISBN
                changeColorElseOptionBtn(view)

            }
            R.id.chatRoomNameOption_btn ->{
                binding.optionFrame.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
                optionType = SearchOptionType.CHATROOMNAME
                changeColorElseOptionBtn(view)
            }
        }
    }

    fun changeColorElseOptionBtn(view : View){
        var textview = view as TextView
        textview.setTextColor(Color.parseColor("#12121D"))
    }


    override fun onRestart() {
        Log.d(TAG, "SearchActivity: onRestart() - 생명주기")
        //Restart() -> Start() -> Resume() 순서로 돌아옴 (돌아올 때 RecyclerView 갱신해야함)
        super.onRestart()
    }

    override fun onStart() {
        Log.d(TAG, "SearchActivity: onStart() - 생명주기")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "SearchActivity: onResume() - 생명주기")
        super.onResume()

        //EditText 초기화
        clearSearchText()

        //리사이클러뷰 갱신
        searchHistoryAdapter.searchHistoryList = SharedPreferenceManager.getSearchHistory()
        searchHistoryAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        Log.d(TAG, "SearchActivity: onPause() - 생명주기")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "SearchActivity: onStop() - 생명주기")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "SearchActivity: onDestroy() - 생명주기")
        super.onDestroy()
    }
}