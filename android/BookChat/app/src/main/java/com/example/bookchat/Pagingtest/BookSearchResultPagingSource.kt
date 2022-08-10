package com.example.bookchat.Pagingtest

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookchat.api.ApiInterface
import com.example.bookchat.data.Book
import com.example.bookchat.utils.Constants.TAG
import retrofit2.HttpException
import java.io.IOException


private const val STARTING_PAGE_INDEX = 1
/*
Api : 데이터를 제공하는 인스턴스
query : 쿼리를 위한 값
 */
class BookSearchResultPagingSource(
    private val Api : ApiInterface,
    private val query : String
): PagingSource<Int, Book>() {

    // load() : 데이터 로드 (사용자가 스크롤 할 때마다 데이터를 비동기적으로 가져온다.)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        // LoadParams : 로드할 키와 항목 수
        // [params.key : 현재 페이지 인덱스를 관리 (처음 데이터를 로드할 때에는 null이 반환)]
        // [params.loadSize : 가져올 데이터의 갯수를 관리]
        // LoadResult : load 작업의 결과
        // [LoadResult.Page : 로드에 성공한 경우, 데이터와 이전 다음 페이지 Key가 포함]
        // [LoadResult.Error : 오류가 발생한 경우]

        // 키 값이 없을 경우 기본값을 사용함
        val page = params.key ?: STARTING_PAGE_INDEX
        params.loadSize

        // 데이터를 제공하는 인스턴스의 메소드 사용
        val response = Api.getBookFromTitle(
            title = query
        )

        val book = response?.body()

        /* 로드에 성공 시 LoadResult.Page 반환
        data : 전송되는 데이터
        prevKey : 이전 값 (위 스크롤 방향)
        nextKey : 다음 값 (아래 스크롤 방향)
        */

        return try {
            LoadResult.Page(
                data = book!!,
                prevKey = if (page == 1) null else page - 1,
                nextKey = page + 1 // 계속 반복되게 해둠
                //nextKey = if (bookSearchResultDtos.isEmpty()) null else page + (params.loadSize / 10)
            )
            // 로드에 실패 시 LoadResult.Error 반환
        } catch (exception: IOException) {
            Log.d(TAG, "BookSearchResultPagingSource: load() - exception : ${exception.message}")
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.d(TAG, "BookSearchResultPagingSource: load() - exception : ${exception.message}")
            LoadResult.Error(exception)
        }
    }
//getRefreshKey() : 스와이프 Refresh나 데이터 업데이트 등으로 현재 목록을 대체할 새 데이터를 로드할 때 사용
    //가장 최근에 접근한 인덱스인 anchorPosition으로 주변 데이터를 다시 로드한다.
// 데이터가 새로고침되거나 첫 로드 후 무효화되었을 때 키를 반환하여 load()로 전달
    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}