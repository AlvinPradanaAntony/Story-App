package com.devcode.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.devcode.storyapp.model.UserPreferences
import com.devcode.storyapp.remote.ApiService
import com.devcode.storyapp.remote.ListStoryItem
import kotlinx.coroutines.flow.first

class StoryPagingSource(private val apiService: ApiService,  private val pref: UserPreferences) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val token = pref.getUser().first().token
            val responseData = apiService.getListStoryWithPaging("Bearer $token",page, params.loadSize).listStory

            LoadResult.Page(
                data = responseData,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (responseData.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}