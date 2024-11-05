package com.zuba.stroyapp2.pagging

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.zuba.stroyapp2.database.StoryDatabase
import com.zuba.stroyapp2.network.ApiService
import com.zuba.stroyapp2.response.ListStory

class StoryRepository(private val apiService: ApiService,private val token:String,private val database: StoryDatabase) {
    fun getStories(): LiveData<PagingData<ListStory>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
            ),
            remoteMediator = RemoteMediator(database, apiService, token),
            pagingSourceFactory = {
                PaggingSource(apiService, token)
            }
        ).liveData
    }
}