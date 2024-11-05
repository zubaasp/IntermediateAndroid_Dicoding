package com.zuba.stroyapp2.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zuba.stroyapp2.Event
import com.zuba.stroyapp2.network.ApiConfig
import com.zuba.stroyapp2.response.ListStory
import com.zuba.stroyapp2.response.StoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsVM : ViewModel() {
    private val _stories = MutableLiveData<List<ListStory>>()
    val stories: LiveData<List<ListStory>> = _stories

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    fun getStoriesToMaps(token: String) {
        val client = ApiConfig.getApiService().getStories("Bearer $token", location = 1)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    _stories.value = response.body()?.listStory
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), StoryResponse::class.java)
                    _message.value = Event(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _message.value = Event(t.message.toString())
            }

        })
    }
}
