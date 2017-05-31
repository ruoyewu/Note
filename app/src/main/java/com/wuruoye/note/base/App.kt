package com.wuruoye.note.base

import android.app.Application
import com.liulishuo.filedownloader.FileDownloader
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.FontCache
import com.wuruoye.note.util.FontUtil

/**
 * Created by wuruoye on 2017/5/26.
 * this file is to do
 */
class App : Application(){

    override fun onCreate() {
        super.onCreate()

        FileDownloader.init(this)

        val fontCache = FontCache(this)
        if (fontCache.font > 0){
            FontUtil.setFont(this,false,Config.fontNameList[fontCache.font - 1])
        }
    }
}