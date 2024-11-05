package com.zuba.stroyapp2.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.zuba.stroyapp2.R
import com.zuba.stroyapp2.database.UserPreference
import com.zuba.stroyapp2.databinding.ActivityDetailBinding
import com.zuba.stroyapp2.model.DetailVM
import com.zuba.stroyapp2.model.UserModelPreference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailVM>()
    private lateinit var mUserPreference: UserPreference
    private lateinit var mUserModelPreference: UserModelPreference

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mUserPreference = UserPreference(this)
        mUserModelPreference = mUserPreference.getLogin()
        resultData()
        binding.btnBack.setOnClickListener{finish()}

    }
    fun resultData(){
        val id = intent.getStringExtra(MainActivity.EXTRA_ID)
        viewModel.getStory(id.toString(), token = mUserModelPreference.token.toString())
        viewModel.story.observe(this@DetailActivity){
            Glide.with(this@DetailActivity).load(it.photoUrl).into(binding.imgStory)
            binding.txtName.text = it.name
            binding.txtDesc.text = it.description
            binding.txtTime.text = it.createdAt.withDateFormat()

        }
    }
    private fun String.withDateFormat(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = format.parse(this) as Date
        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    }
}