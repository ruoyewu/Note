package com.wuruoye.note.util

import android.content.Context
import android.graphics.Typeface
import android.os.Environment
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.wuruoye.note.model.Config

/**
 * Created by wuruoye on 2017/5/30.
 * this file is to do
 */
object FontUtil {
    private val fontPath = "/com.wuruoye.note/"

    private fun getFontPath(fontName: String): String{
        val path = Environment.getExternalStorageDirectory().absolutePath + fontPath + fontName
        return path
    }

    fun setFont(context: Context, isAssets: Boolean, name: String){
        val path = getFontPath(name)
        val typeFace =
                if (isAssets){
                    Typeface.createFromAsset(context.assets,path)
                }else{
                    Typeface.createFromFile(path)
                }
        val field = Typeface::class.java.getDeclaredField("SERIF")
        field.isAccessible = true
        field.set(null, typeFace)
    }

    fun downloadFont(name: String, listener: FileDownloadListener){
        val path = getFontPath(name)
        FileUtil.downloadFile(Config.baseUrl + name,path, listener)
    }
}