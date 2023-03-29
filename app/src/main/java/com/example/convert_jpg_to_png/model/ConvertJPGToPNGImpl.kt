package com.example.convert_jpg_to_png.model


import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.example.convert_jpg_to_png.App
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class ConvertJPGToPNGImpl: ConvertJPGToPNG {

    override fun convertImage(name: String, fileNamePNG: String): Boolean {

        var result: Boolean = false
        val picBitmap: Bitmap
        val outStream: FileOutputStream
        try {
            picBitmap = BitmapFactory.decodeFile(name)
            outStream = FileOutputStream(File(fileNamePNG))
            result = picBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()
        } catch (exception: FileNotFoundException) {
            exception.printStackTrace()
        }
        return result
    }

    override fun getPathsPNG(imageUri: String): String {

        val fileImageJPG = File(imageUri)
        val pathForPNG = fileImageJPG.path.substring(0, fileImageJPG.path.lastIndexOf("/"))

        return pathForPNG + "/" + fileImageJPG.nameWithoutExtension + ".png"
    }

    override fun getRealPath(contentURI: String): String {

        val cursor: Cursor? =
            App.instance.contentResolver.query(Uri.parse(contentURI), null, null, null, null)
        val resultPath = if (cursor == null) {
            contentURI
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }
        cursor?.close()
        return resultPath
    }
}