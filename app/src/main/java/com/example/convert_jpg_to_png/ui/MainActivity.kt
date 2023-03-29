package com.example.convert_jpg_to_png.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.convert_jpg_to_png.App
import com.example.convert_jpg_to_png.R
import com.example.convert_jpg_to_png.databinding.ActivityMainBinding
import com.example.convert_jpg_to_png.presenter.MainPresenter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity(), UserView {

    companion object {

        var PERMISSIONS = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var resultImage: ActivityResultLauncher<Intent>
    private lateinit var imageUri: Uri
    private lateinit var dialog: AlertDialog

    val presenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.registerActivityResults()

        presenter.setConvertButtonVisible(false)

        binding.convertButton.setOnClickListener {
            presenter.convert(imageUri)
        }

        binding.getPic.setOnClickListener {
            requestImage()
        }
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                requestImage()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                showRationalDialog()
            }

            else -> {
                requestPermissionLauncher.launch(PERMISSIONS)
            }
        }
    }

    private fun showDialogAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(R.string.dialog_button_close) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showRationalDialog() {

        AlertDialog.Builder(this)
            .setTitle("")
            .setMessage("")
            .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ ->
                {
                    checkPermissions()
                }
            }.setNegativeButton(R.string.dialog_rationale_decline) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun requestImage() {

        if (!hasPermission(PERMISSIONS)) {
            checkPermissions()
        } else {
            val photoGetIntent = Intent(Intent.ACTION_PICK)

            photoGetIntent.type = "image/*"
            photoGetIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpg"))

            resultImage.launch(photoGetIntent)
        }
    }

    private fun hasPermission(permissions: Array<String>): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun registerActivityResults() {
        resultImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                Completable.create { emitter ->

                    if (result.resultCode == Activity.RESULT_OK) {
                        emitter.onComplete()
                        result.data?.data?.let {
                            imageUri = it
                        }
                    } else {
                        emitter.onError(Throwable("Error picking image"))
                    }
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        presenter.setImage(imageUri)
                        presenter.setConvertButtonVisible(true)
                    }
                    .subscribe()

            }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

                    permissions ->
                val granted = permissions.entries.all {
                    it.value == true
                }
                if (granted) {
                    requestImage()
                } else {
                    showDialogAlert(getString(R.string.dialog_title_no_access_to_file_system),
                        getString(R.string.dialog_message_no_file_system_access))
                }
            }
    }

    override fun setImage(path: Uri) {
        binding.selectedImage.setImageURI(path)
    }

    override fun setPngImage(pathOfPNG: String) {
        binding.convertedImage.setImageURI(Uri.parse(pathOfPNG))
    }

    override fun showDialog() {

        val builder = AlertDialog.Builder(this)
        val customLayout = layoutInflater.inflate(R.layout.custom_dialog, null);
        builder.setView(customLayout)

        dialog = builder
            .setTitle(title)
            .setMessage(R.string.dialog_run_converting_text)
            .setNegativeButton(R.string.dialog_button_cancel) { dialog, _ ->
                presenter.cancelConvert()
            }.show()
    }

    override fun closeDialog() {
        dialog.dismiss()
    }

    override fun setStatusText(it: Boolean) {
        if (it) {
            binding.convertStatus.text = getString(R.string.success)
        } else {
            binding.convertStatus.text = getString(R.string.error)
        }
    }

    override fun convertButtonVisibility(b: Boolean) {
        if (b) {
            binding.convertButton.visibility = View.VISIBLE
        } else {
            binding.convertButton.visibility = View.GONE
        }
    }
}