package com.zuba.stroyapp2.pagging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.zuba.stroyapp2.database.StoryDatabase
import com.zuba.stroyapp2.model.RemoteKeys
import com.zuba.stroyapp2.network.ApiService
import com.zuba.stroyapp2.response.ListStory
import retrofit2.awaitResponse

@OptIn(ExperimentalPagingApi::class)
class RemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, ListStory>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStory>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getStories(token = token, size = 5, page = page).awaitResponse()
            if (responseData.isSuccessful) {
                val data = responseData.body()
                return if (data != null) {
                    val endOfPaginationReached = data.listStory.isEmpty()

                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            database.remoteKeysDAO().deleteRemoteKeys()
                            database.storyDao().deleteAll()
                        }

                        val prevKey = if (page == 1) null else page -1
                        val nextKey = if (endOfPaginationReached) null else page + 1
                        val keys = data.listStory.map {
                            RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                        }
                        database.remoteKeysDAO().insertAll(keys)
                        database.storyDao().insertStory(data.listStory)
                    }

                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                } else {
                    MediatorResult.Error(Exception())
                }
            } else {
                return MediatorResult.Error(Exception())
            }
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStory>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDAO().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStory>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDAO().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStory>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDAO().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}