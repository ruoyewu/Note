package com.wuruoye.note.base

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by wuruoye on 2017/5/29.
 * this file is to do
 */

open class BaseCache(context: Context) {
    protected var mSP: SharedPreferences? = null

    init {
        if (mSP == null) {
            synchronized(this) {
                if (mSP == null) {
                    mSP = context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                }
            }
        }
    }

    companion object {
        private val spName = "ruoye.note"
    }
}
