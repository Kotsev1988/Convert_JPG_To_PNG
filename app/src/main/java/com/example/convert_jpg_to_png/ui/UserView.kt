package com.example.convert_jpg_to_png.ui

import android.net.Uri

interface UserView {
    fun registerActivityResults()
    fun setImage(path: Uri)
    fun setPngImage(pathOfPNG: String)
    fun showDialog()
    fun closeDialog()
     fun setStatusText(it: Boolean)
     fun convertButtonVisibility(b: Boolean)
}