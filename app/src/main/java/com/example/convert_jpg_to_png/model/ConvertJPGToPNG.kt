package com.example.convert_jpg_to_png.model

interface ConvertJPGToPNG {
    fun convertImage(name: String, fileNamePNG: String): Boolean
    fun getPathsPNG(imageUri: String): String
    fun getRealPath(contentURI: String): String
}