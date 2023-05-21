package com.devcode.storyapp

import com.devcode.storyapp.db.StoryResponseRoom

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryResponseRoom> {
        val storyList: MutableList<StoryResponseRoom> = arrayListOf()
        val ranges = 0..10
        for (i in ranges) {
            val story = StoryResponseRoom(
                "id + $i",
                "photo + $i",
                "createdAt + $i",
                "name + $i",
                "description + $i",
                0.0 + i,
                0.0 + i
            )
            storyList.add(story)
        }
        return storyList
    }
}