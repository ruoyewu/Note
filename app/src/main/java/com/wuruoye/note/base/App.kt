package com.wuruoye.note.base

import android.app.Application
import com.liulishuo.filedownloader.FileDownloader
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.FontCache
import com.wuruoye.note.util.FontUtil
import com.wuruoye.note.util.toast

/**
 * Created by wuruoye on 2017/5/26.
 * this file is to do
 */
class App : Application(){

    override fun onCreate() {
        super.onCreate()

        FileDownloader.init(this)

        val fontCache = FontCache(this)
        val font = fontCache.font
        if (font > 0){
            try {
                FontUtil.setFont(this,false,Config.fontNameList[font - 1])
            } catch(e: Exception) {
                toast("字体加载出错...请稍后重新下载")
                fontCache.font = 0
                val list = fontCache.getFontList()
                if (list.contains(font)){
                    list.remove(font)
                }
                fontCache.setFontList(list)
            }
        }
    }
}