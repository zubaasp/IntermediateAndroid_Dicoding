package com.zuba.stroyapp2.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zuba.stroyapp2.R
import com.zuba.stroyapp2.adapter.LoadingAdapter
import com.zuba.stroyapp2.adapter.MainAdapter
import com.zuba.stroyapp2.database.UserPreference
import com.zuba.stroyapp2.databinding.ActivityMainBinding
import com.zuba.stroyapp2.model.MainVM
import com.zuba.stroyapp2.model.UserModelPreference
import com.zuba.stroyapp2.model.ViewModelFactory
import com.zuba.stroyapp2.response.ListStory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mUserPreference: UserPreference
    private lateinit var mUserModelPreference: UserModelPreference
    private val viewModel: MainVM by viewModels {
        ViewModelFactory(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mUserPreference = UserPreference(this)
        mUserModelPreference = mUserPreference.getLogin()
        getUserLogin()

        val layoutManager = LinearLayoutManager(this)
        binding.listStory.layoutManager = layoutManager
        result()
        menuOption()
        binding.btnAdd.setOnClickListener{
            startActivity(Intent(this@MainActivity,AddStoryActivity::class.java))
        }
    }
    fun getUserLogin(){
        if(mUserPreference.getLogin().token==""){
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            finish()
        }
    }
    fun result() {
        val adapter = MainAdapter()
        binding.listStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingAdapter {
                adapter.retry()
            }
        )

        viewModel.stories.observe(this@MainActivity) { data ->
            adapter.submitData(lifecycle, data)
            adapter.setOnItemClickListener(object : MainAdapter.OnItemClickListener {
                override fun onItemClicked(data: ListStory) {
                  //  Toast.makeText(this@MainActivity, "", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity,DetailActivity::class.java)
                    intent.putExtra(EXTRA_ID,data.id)
                    startActivity(intent)
                }

            })
        }
    }
    fun menuOption(){
        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btn_logout -> {
                  mUserPreference.clearLogin()
                    startActivity(Intent(this@MainActivity,LoginActivity::class.java))
                    true
                }
                R.id.btn_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                R.id.btn_maps -> {
                    startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
    companion object {
        const val EXTRA_ID = "extra_id"
    }
}