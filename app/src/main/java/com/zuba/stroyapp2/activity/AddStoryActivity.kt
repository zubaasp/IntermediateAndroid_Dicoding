package com.zuba.stroyapp2.activity

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.zuba.stroyapp2.R
import com.zuba.stroyapp2.databinding.ActivityAddStoryBinding
import com.zuba.stroyapp2.model.AddStoryVM
import com.zuba.stroyapp2.utils.rotateFile
import com.zuba.stroyapp2.utils.uriToFile
import java.io.File
import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.zuba.stroyapp2.utils.createCustomTempFile
import android.content.Intent.ACTION_GET_CONTENT
import android.view.View
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.zuba.stroyapp2.database.UserPreference
import com.zuba.stroyapp2.model.LoginVM
import com.zuba.stroyapp2.model.UserModelPreference
import com.zuba.stroyapp2.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var userPreference: UserPreference
    private lateinit var userModelPreference: UserModelPreference
    private var getFile: File? = null
    private val viewModel by viewModels<AddStoryVM>()
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(file)
                getFile = file
                binding.cameraResult.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }
    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage = result.data?.data as Uri
            selectedImage.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.cameraResult.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreference = UserPreference(this)
        userModelPreference = userPreference.getLogin()
        binding.btnCamera.setOnClickListener{
            startCamera()
        }
        binding.btnGallery.setOnClickListener{
            startGallery()
        }
        binding.btnUpload.setOnClickListener {
            uploadStory()
        }
        binding.btnBack.setOnClickListener{
            finish()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.error_camera_permission), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all { msg ->
        ContextCompat.checkSelfPermission(baseContext, msg) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(this@AddStoryActivity, "com.zuba.stroyapp2", it)
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.gallery_title))
        launcherIntentGallery.launch(chooser)
    }
    private fun uploadStory(){
        val description = binding.txtDesc.text.toString()

        val file = reduceFileImage(getFile as File)
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

        viewModel.uploadStory(imageMultipart, requestDescription, userModelPreference.token.toString())
        viewModel.isLoading.observe(this) { showLoading(it) }
        viewModel.snackbarText.observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                if (text == "") {
                    val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                    intent.putExtra(EXTRA_MESSAGE, getString(R.string.success_create))
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    Snackbar.make(window.decorView.rootView, text, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            ProgressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
            btnCamera.isEnabled = !isLoading
            btnGallery.isEnabled = !isLoading
            btnUpload.isEnabled = !isLoading
            txtDesc.isEnabled = !isLoading
        }
    }
    companion object {
        const val EXTRA_MESSAGE = "extra_msg"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }


}