package com.example.convert_jpg_to_png.presenter

import android.media.ExifInterface
import android.net.Uri
import com.example.convert_jpg_to_png.model.ConvertJPGToPNG
import com.example.convert_jpg_to_png.ui.UserView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MainPresenter(var userView: UserView) {

    private val model = ConvertJPGToPNG()
     var  dispasable = CompositeDisposable()

    fun registerActivityResults() {
        userView.registerActivityResults()
    }

    fun setImage(uri: Uri) {
        userView.setImage(uri)
    }

    fun convert(uri: Uri) {
        userView.showDialog()
        var realPathJPG: String = ""
        var pathOfPNG: String = ""

        val disposable1 = Observable.fromCallable {
                realPathJPG = model.getRealPath(uri)
                pathOfPNG = model.getPathsPNG(realPathJPG)
               return@fromCallable model.convertImage(realPathJPG, pathOfPNG)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                userView.closeDialog()
            }
            .subscribe{
                if (it){
                    userView.setStatusText(it)
                    userView.convertButtonVisibility(false)
                    userView.setPngImage(pathOfPNG)
                }else{
                    userView.setStatusText(it)
                }
            }

        dispasable.addAll(disposable1)

    }

    fun cancelConvert(){

        dispasable.dispose()
        dispasable.clear()
        userView.closeDialog()
    }

    fun setConvertButtonVisible(b: Boolean) {
        userView.convertButtonVisibility(b)
    }
}
