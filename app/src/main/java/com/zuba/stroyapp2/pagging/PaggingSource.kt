package com.zuba.stroyapp2.pagging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zuba.stroyapp2.network.ApiService
import com.zuba.stroyapp2.response.ListStory
import retrofit2.awaitResponse

class PaggingSource(private val apiService: ApiService, private val header: String) : PagingSource<Int, ListStory>() {
    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        val position = params.key ?: INITIAL_PAGE_INDEX
        return try {
            val responseData = apiService.getStories(header, size = 5, page = position).awaitResponse()
            if (responseData.isSuccessful) {
                val data = responseData.body()
                return if (data != null) {
                    LoadResult.Page(
                        data = data.listStory,
                        prevKey = if (position == INITIAL_PAGE_INDEX) null else position,
                        nextKey = if (data.listStory.isEmpty()) null else position + 1
                    )
                } else {
                    LoadResult.Error(Exception())
                }
            } else {
                LoadResult.Error(Exception())
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}