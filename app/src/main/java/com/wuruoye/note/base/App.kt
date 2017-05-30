package com.wuruoye.note.base

import android.app.Application
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.NoteCache
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created by wuruoye on 2017/5/26.
 * this file is to do
 */
class App : Application(){

    override fun onCreate() {
        super.onCreate()

        val noteCache = NoteCache(this)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath(Config.fontList[noteCache.fontShow])
                .build()
        )
    }
}