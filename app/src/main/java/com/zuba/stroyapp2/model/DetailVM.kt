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

class DetailVM: ViewModel() {
    private val _story = MutableLiveData<ListStory>()
    val story: LiveData<ListStory> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun getStory(id: String, token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStory(id, "Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        response.body()?.listStory
                    }
                    _story.value = response.body()?.story
                    _snackbarText.value =
                        Event(if (response.body()?.error == true) response.body()?.message.toString() else "")
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), StoryResponse::class.java)
                    _snackbarText.value = Event(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event(t.message.toString())
            }

        })
    }

}