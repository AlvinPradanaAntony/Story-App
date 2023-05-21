package com.devcode.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.devcode.storyapp.DataDummy
import com.devcode.storyapp.MainDispatcherRule
import com.devcode.storyapp.adapter.PagingStoryAdapter
import com.devcode.storyapp.data.RepositoryStory
import com.devcode.storyapp.db.StoryResponseRoom
import com.devcode.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var mainViewModel: MainViewModel

    @Mock
    private lateinit var repositoryStory: RepositoryStory
    private val dummyStoryList = DataDummy.generateDummyStoryResponse()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(repositoryStory)
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest{
        val data: PagingData<StoryResponseRoom> = PagingSourceTest.snapshot(dummyStoryList)
        val expectedResult = MutableLiveData<PagingData<StoryResponseRoom>>()
        expectedResult.value = data
        Mockito.`when`(repositoryStory.getStory()).thenReturn(expectedResult)

        val actualStory: PagingData<StoryResponseRoom> = mainViewModel.getStory().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = PagingStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Mockito.verify(repositoryStory).getStory()
        assertNotNull(differ.snapshot())
        assertEquals(dummyStoryList, differ.snapshot())
        assertEquals(dummyStoryList.size, differ.snapshot().size)
        assertEquals(dummyStoryList[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story No Data Should Return Empty List`() = runTest {
        val emptyData: PagingData<StoryResponseRoom> = PagingData.empty()
        val expectedResult = MutableLiveData<PagingData<StoryResponseRoom>>()
        expectedResult.value = emptyData
        Mockito.`when`(repositoryStory.getStory()).thenReturn(expectedResult)

        val actualStory: PagingData<StoryResponseRoom> = mainViewModel.getStory().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = PagingStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Mockito.verify(repositoryStory).getStory()
        assertNotNull(differ.snapshot())
        assertTrue(differ.snapshot().isEmpty())
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}


class PagingSourceTest : PagingSource<Int, LiveData<List<StoryResponseRoom>>>() {
    companion object {
        fun snapshot(items: List<StoryResponseRoom>): PagingData<StoryResponseRoom> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryResponseRoom>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryResponseRoom>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}