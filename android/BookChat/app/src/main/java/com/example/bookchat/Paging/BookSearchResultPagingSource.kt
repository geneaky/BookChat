package com.example.bookchat.Paging

import android.widget.Toast
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookchat.App
import com.example.bookchat.api.ApiInterface
import com.example.bookchat.data.Book
import com.example.bookchat.data.BookSearchOption

private const val STARTING_PAGE_INDEX = 1

class BookSearchResultPagingSource(
    private val Api : ApiInterface,
    private val bookSearchOption: BookSearchOption,
    private val searchResultCountCallBack : (Int) -> Unit
): PagingSource<Int, Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val loadSize = if(params.loadSize == 60 ) 20 else 20

        try {
            if (!App.instance.isNetworkConnected()){
                throw Exception("네트워크가 연결되어 있지 않습니다")
            }
        }catch (e: Exception){
            Toast.makeText(App.instance.applicationContext,"${e.message}", Toast.LENGTH_SHORT).show()
            return LoadResult.Error(e)
        }

        //통합 api로 수정해야함
        val response = Api.getBookFromTitle(
            title = bookSearchOption.keyWord,
            size = loadSize.toString(),
            page = page.toString(),
            sort = bookSearchOption.sort
        )
        val bookSearchResultDto = response.body()
        searchResultCountCallBack(bookSearchResultDto!!.meta.totalCount)

        return try {
            LoadResult.Page(
                data = bookSearchResultDto.books,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if(bookSearchResultDto.meta.isEnd == true) null else page + 1
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    // 데이터가 새로고침되거나 첫 로드 후 무효화되었을 때 키를 반환하여 load()로 전달 (LoadParams에 PageKey를 전달할 때 사용하는 함수)
    // previousKey가 null이면 첫번째 페이지를 반환하고 nextKey가 null이면 마지막 페이지를 반환 만약 둘 다 null이면 null을 반환
    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}