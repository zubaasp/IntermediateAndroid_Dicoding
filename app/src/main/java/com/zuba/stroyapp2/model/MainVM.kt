package com.zuba.stroyapp2.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zuba.stroyapp2.di.Injection
import com.zuba.stroyapp2.pagging.StoryRepository
import com.zuba.stroyapp2.response.ListStory

class MainVM(storyRepository: StoryRepository) : ViewModel() {
    val stories: LiveData<PagingData<ListStory>> = storyRepository.getStories().cachedIn(viewModelScope)
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainVM(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}