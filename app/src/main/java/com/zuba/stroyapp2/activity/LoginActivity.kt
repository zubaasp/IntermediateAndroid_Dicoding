package com.zuba.stroyapp2.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.zuba.stroyapp2.database.UserPreference
import com.zuba.stroyapp2.databinding.ActivityLoginBinding
import com.zuba.stroyapp2.model.LoginVM
import com.zuba.stroyapp2.model.UserModelPreference

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val LoginVM by viewModels<LoginVM>()
    private lateinit var userModelPreference: UserModelPreference
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreference = UserPreference(this@LoginActivity)
        userModelPreference = userPreference.getLogin()
        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
        }
        binding.btnLogin.setOnClickListener{
            loginUser()
        }

    }
    private fun setUserToken(userId:String, name:String, token:String){
        val userPreference = UserPreference(this)
        userModelPreference.userId = userId
        userModelPreference.name = name
        userModelPreference.token = token
        userPreference.setLogin(userModelPreference)
    }
    private fun loginUser() {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        LoginVM.isLoading.observe(this@LoginActivity) { showLoading(it) }
        LoginVM.snackbarText.observe(this@LoginActivity){
            it.getContentIfNotHandled()?.let { text ->
                Toast.makeText(this@LoginActivity, text, Toast.LENGTH_SHORT).show() }
        }
        LoginVM.userLogin(email, password)
        LoginVM.loginResult.observe(this@LoginActivity) {data ->
            setUserToken(data.userId,data.name,data.token)
            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}