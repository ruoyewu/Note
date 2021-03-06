package com.wuruoye.note.base

import android.app.Application
import android.graphics.Typeface
import com.droi.sdk.core.Core
import com.droi.sdk.core.DroiCloud
import com.droi.sdk.core.DroiObject
import com.droi.sdk.core.DroiUser
import com.liulishuo.filedownloader.FileDownloader
import com.wuruoye.note.model.*
import com.wuruoye.note.util.FontUtil
import com.wuruoye.note.util.toast

/**
 * Created by wuruoye on 2017/5/26.
 * this file is to do
 */
class App : Application(){

    override fun onCreate() {
        super.onCreate()

        //文件下载初始化
        FileDownloader.init(this)

        //云服务初始化
        DroiUser.setAutoAnonymousUser(false)
        DroiObject.registerCustomClass(UpNote::class.java)
        DroiObject.registerCustomClass(Feedback::class.java)
        Core.initialize(this)

        //字体初始化
        val fontCache = FontCache(this)
        val config = resources.configuration
        config.fontScale = fontCache.fontSize / 15
        resources.updateConfiguration(config, resources.displayMetrics)
        val font = fontCache.font
        if (font > 0){
            try {
                mTypeFace = FontUtil.setFont(this,false,Config.fontNameList[font - 1])
            } catch(e: Exception) {
                toast("字体加载出错...请稍后重新下载")
                fontCache.font = 0
                FontUtil.deleteFontFromStorage(Config.fontNameList[font - 1])
            }
        }
    }

    companion object {
        var mTypeFace: Typeface? = null
    }
}