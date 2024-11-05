package com.zuba.stroyapp2.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.zuba.stroyapp2.R
import com.zuba.stroyapp2.databinding.ActivityRegisterBinding
import com.zuba.stroyapp2.model.RegisterVM

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val RegisterVM by viewModels<RegisterVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener{
            registerUser()
        }

    }
    private fun registerUser() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        RegisterVM.snackbarText.observe(this@RegisterActivity){
            it.getContentIfNotHandled()?.let { text ->
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            }
        }
        RegisterVM.isLoading.observe(this@RegisterActivity) {
            showLoading(it)
            if (it==false && (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()))
                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
        }

        RegisterVM.userRegister(name, email, password)

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}