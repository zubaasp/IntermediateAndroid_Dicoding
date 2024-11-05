package com.zuba.stroyapp2.response

import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @field:SerializedName("listStory")
    val listStory: List<ListStory>,

    @field:SerializedName("story")
    val story: ListStory,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)