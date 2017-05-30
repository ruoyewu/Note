package com.wuruoye.note.base

import android.app.Application
import com.liulishuo.filedownloader.FileDownloader

/**
 * Created by wuruoye on 2017/5/26.
 * this file is to do
 */
class App : Application(){

    override fun onCreate() {
        super.onCreate()

        FileDownloader.init(this)
    }
}