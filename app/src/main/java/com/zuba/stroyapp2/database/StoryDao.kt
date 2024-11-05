package com.zuba.stroyapp2.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zuba.stroyapp2.response.ListStory

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(quote: List<ListStory>)

    @Query("SELECT * FROM stories")
    fun getAllStory(): PagingSource<Int, ListStory>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}