package com.example.convert_jpg_to_png

import android.app.Application
import android.content.ContentResolver
import android.content.Context

class App: Application() {


 override fun onCreate() {
  super.onCreate()
  instance = this
 }

 companion object {
  lateinit var instance: App
   private set
 }
}

