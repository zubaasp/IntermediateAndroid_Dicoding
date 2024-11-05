package com.zuba.stroyapp2.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zuba.stroyapp2.Event
import com.zuba.stroyapp2.network.ApiConfig
import com.zuba.stroyapp2.response.LoginRequest
import com.zuba.stroyapp2.response.LoginResponse
import com.zuba.stroyapp2.response.LoginResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginVM : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun userLogin(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(
            LoginRequest(email, password)
        )
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _loginResult.value = response.body()?.loginResult
                    _snackbarText.value = Event(response.body()?.message.toString())
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), LoginResponse::class.java)
                    _snackbarText.value = Event(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event(t.message.toString())
            }

        })
    }
}