package com.zuba.stroyapp2.data

import com.zuba.stroyapp2.response.ListStory

object DummyData {
    fun generateDummyStoryEntity(): List<ListStory> {
        val storyList: MutableList<ListStory> = arrayListOf()
        for (i in 0..5) {
            val stories = ListStory(
                "id-$i",
                "Title $i",
                "Description $i",
                "https://story-api.dicoding.dev/images/stories/photos-1683615858411_OkjiCYL1.jpg",
                "2023-05-09T07:04:18.412Z"
            )
            storyList.add(stories)
        }

        return storyList
    }
}