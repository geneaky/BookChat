package com.example.bookchat.Paging

import android.util.Log
import android.widget.Toast
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookchat.App
import com.example.bookchat.api.ApiInterface
import com.example.bookchat.data.Book
import com.example.bookchat.data.BookSearchOption
import com.example.bookchat.utils.Constants.TAG
import kotlin.random.Random

// LoadParams : 로드할 키와 항목 수
// [params.key : 현재 페이지 인덱스를 관리 (처음 데이터를 로드할 때에는 null이 반환)]
// [params.loadSize : 가져올 데이터의 갯수를 관리]
// LoadResult : load 작업의 결과
// [LoadResult.Page : 로드에 성공한 경우, 데이터와 이전 다음 페이지 Key가 포함]
// [LoadResult.Error : 오류가 발생한 경우]

/* 로드에 성공 시 LoadResult.Page 반환
data : 전송되는 데이터
prevKey : 이전 값 (위 스크롤 방향)
nextKey : 다음 값 (아래 스크롤 방향)
*/
private const val STARTING_PAGE_INDEX = 1
/*
Api : 데이터를 제공하는 인스턴스
query : 쿼리를 위한 값
 */
class BookSearchResultPagingSource(
    private val Api : ApiInterface,
    private val bookSearchOption: BookSearchOption
): PagingSource<Int, Book>() {

    // load() : 데이터 로드 (사용자가 스크롤 할 때마다 데이터를 비동기적으로 가져온다.)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val loadSize = if(params.loadSize == 60 ) 20 else 20 //Pager에 정의한 pageSize (최초 호출엔 x3되어서 요첨하기 때문에 다시 기본값으로 세팅)

        try {
            if (!App.instance.isNetworkConnected()){
                throw Exception("네트워크가 연결되어 있지 않습니다")
            }
        }catch (e: Exception){
            Toast.makeText(App.instance.applicationContext,"${e.message}", Toast.LENGTH_SHORT).show()
            return LoadResult.Error(e)
        }

        val response = Api.getBookFromTitle(
            title = bookSearchOption.title,
            size = loadSize.toString(),
            page = page.toString(),
            sort = bookSearchOption.sort
        )
        val temp = BookSearchOption(bookSearchOption.title,loadSize.toString(),page.toString(),bookSearchOption.sort)
        Log.d(TAG, "BookSearchResultPagingSource: load() - BookSearchOption : $temp")
        Log.d(TAG, "BookSearchResultPagingSource: load() - response : ${response}")

        val pagedBookList = response.body()?.books //Meta값은 사용하지 않음
        Log.d(TAG, "BookSearchResultPagingSource: load() - response.body() : ${response.body()}")
        Log.d(TAG, "BookSearchResultPagingSource: load() - response.body()?.books 크기(${response.body()?.books?.size}) : ${response.body()?.books}")
        Log.d(TAG, "BookSearchResultPagingSource: load() - response.body()?.meta : ${response.body()?.meta}")

        return try {

            // 에러 발생 !
            if (Random.nextFloat() < 0.5) {
                throw Exception("error !!!")
            }

            LoadResult.Page(
                data = pagedBookList!!,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if(response.body()?.meta?.isEnd == true) null else page + 1
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }
//getRefreshKey() : 스와이프 Refresh나 데이터 업데이트 등으로 현재 목록을 대체할 새 데이터를 로드할 때 사용
    //가장 최근에 접근한 인덱스인 anchorPosition으로 주변 데이터를 다시 로드한다.
    //Adapter.refresh() 를 했을 때/ 무효화 되었을때 getRefreshKey() 가 호출되는데 다시 시작할 key를 반환해주면 된다.
// 데이터가 새로고침되거나 첫 로드 후 무효화되었을 때 키를 반환하여 load()로 전달
    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}