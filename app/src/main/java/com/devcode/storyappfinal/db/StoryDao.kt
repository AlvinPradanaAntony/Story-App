package com.devcode.storyappfinal.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryResponseRoom>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, StoryResponseRoom>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}