package com.zuba.stroyapp2.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.zuba.stroyapp2.adapter.MainAdapter
import com.zuba.stroyapp2.data.DummyData
import com.zuba.stroyapp2.data.MainDispatcherRule
import com.zuba.stroyapp2.pagging.StoryRepository
import com.zuba.stroyapp2.response.ListStory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainVMTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DummyData.generateDummyStoryEntity()
        val data: PagingData<ListStory> = StoryPagingSource.snapshot(dummyStory)
        val expectedData = MutableLiveData<PagingData<ListStory>>()
        expectedData.value = data
        `when`(storyRepository.getStories()).thenReturn(expectedData)

        val homeViewModel = MainVM(storyRepository)
        val actualStory: PagingData<ListStory> = homeViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStory> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStory>>()
        expectedStory.value = data
        `when`(storyRepository.getStories()).thenReturn(expectedStory)

        val mainVM = MainVM(storyRepository)
        val actualStory: PagingData<ListStory> = mainVM.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStory>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStory>>>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStory>>> =
        LoadResult.Page(emptyList(), 0, 1)

    companion object {
        fun snapshot(items: List<ListStory>): PagingData<ListStory> = PagingData.from(items)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}