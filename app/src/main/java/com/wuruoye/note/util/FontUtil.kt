package com.wuruoye.note.util

import android.content.Context
import android.graphics.Typeface
import android.os.Environment
import com.liulishuo.filedownloader.FileDownloadListener
import com.wuruoye.note.model.Config
import java.io.File

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
        FileUtil.downloadFile(Config.Github_baseUrl + name,path, listener)
    }

    fun getFontFromStorage(): ArrayList<Int>{
        val fontList = ArrayList<Int>()
        val file = File(Config.fontPath)
        if (!file.exists()){
            file.mkdirs()
        }
        for (i in file.listFiles()){
            val name = i.name
            if (Config.fontNameList.contains(name)){
                fontList.add(Config.fontNameList.indexOf(name) + 1)
            }
        }
        return fontList
    }

    fun deleteFontFromStorage(name: String){
        val file = File(Config.fontPath + name)
        if (file.exists()){
            file.delete()
        }
    }
}