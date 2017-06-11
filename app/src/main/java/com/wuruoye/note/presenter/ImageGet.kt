package com.wuruoye.note.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.wuruoye.note.R

import com.wuruoye.note.base.IAbsPresenter
import com.wuruoye.note.base.IAbsView
import com.wuruoye.note.base.IBasePresenter
import com.wuruoye.note.base.IListener
import com.wuruoye.note.model.Config
import com.wuruoye.note.util.BitmapOverlay
import com.wuruoye.note.util.ImageCompressUtil
import id.zelory.compressor.Compressor
import java.io.File

/**
 * Created by wuruoye on 2017/6/11.
 * this file is to do
 */

class ImageGet(context: Context) : IAbsPresenter<IAbsView<Bitmap>>(context), IBasePresenter, IListener<Bitmap>{
    override fun onSuccess(model: Bitmap) {
        getView()?.setModel(model)
    }

    override fun onFail(fail: String) {
        getView()?.setWorn(fail)
    }

    fun writeFile(file: File, name: String){
        Thread({
            try {
                val bmp = Compressor.getDefault(mContext)
                        .compressToBitmap(file)
                ImageCompressUtil.writeToFile(bmp, name)

                val bmp1 = BitmapFactory.decodeResource(mContext.resources, R.drawable.paper)
                val bmp2 = BitmapFactory.decodeFile(Config.imagePath + name)
                val bmp3 = BitmapOverlay.overlay(bmp1, bmp2)
                onSuccess(bmp3)
            } catch(e: Exception) {
                onFail("图片加载失败,请重试")
            }
        }).start()
    }

    fun readFile(name: String){
        Thread({
            try {
                val bmp1 = BitmapFactory.decodeResource(mContext.resources, R.drawable.paper)
                val bmp2 = BitmapFactory.decodeFile(Config.imagePath + name)
                val bmp3 = BitmapOverlay.overlay(bmp1, bmp2)
                onSuccess(bmp3)
            } catch(e: Exception) {
                onFail("图片加载失败,请重试")
            }
        }).start()
    }



}
