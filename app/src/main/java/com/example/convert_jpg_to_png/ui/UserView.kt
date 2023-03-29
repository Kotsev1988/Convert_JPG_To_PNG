package com.example.convert_jpg_to_png.ui



interface UserView {
    fun registerActivityResults()
    fun setImage(path: String)
    fun setPngImage(pathOfPNG: String)
    fun showDialog()
    fun closeDialog()
     fun setStatusText(it: Boolean)
     fun convertButtonVisibility(b: Boolean)
}