package com.zuba.stroyapp2.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zuba.stroyapp2.Event
import com.zuba.stroyapp2.network.ApiConfig
import com.zuba.stroyapp2.response.RegisterResponse
import com.zuba.stroyapp2.response.RegistrasiRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterVM : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText
    fun userRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(
            RegistrasiRequest(name, email, password)
        )
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _snackbarText.value = Event(response.body()?.message.toString())
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), RegisterResponse::class.java)
                    _snackbarText.value = Event(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event(t.message.toString())
            }
        })
    }
}