package com.zuba.stroyapp2.di

import android.content.Context
import com.zuba.stroyapp2.database.StoryDatabase
import com.zuba.stroyapp2.database.UserPreference
import com.zuba.stroyapp2.network.ApiConfig
import com.zuba.stroyapp2.pagging.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val token = UserPreference(context).getLogin().token.toString()
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository(apiService, "Bearer $token", database)
    }
}