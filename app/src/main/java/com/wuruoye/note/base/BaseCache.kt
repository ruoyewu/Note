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

    protected fun getInt(key: String, default: Int): Int{
        return mSP!!.getInt(key, default)
    }
    protected fun getBoolean(key: String, default: Boolean): Boolean{
        return mSP!!.getBoolean(key, default)
    }
    protected fun getString(key: String, default: String): String{
        return mSP!!.getString(key, default)
    }
    protected fun getFloat(key: String, default: Float): Float{
        return mSP!!.getFloat(key, default)
    }
    protected fun getLong(key: String, default: Long): Long{
        return mSP!!.getLong(key, default)
    }

    protected fun putInt(key: String, value: Int){
        mSP!!.edit().putInt(key, value).apply()
    }
    protected fun putBoolean(key: String, value: Boolean){
        mSP!!.edit().putBoolean(key, value).apply()
    }
    protected fun putString(key: String, value: String){
        mSP!!.edit().putString(key, value).apply()
    }
    protected fun putFloat(key: String, value: Float){
        mSP!!.edit().putFloat(key, value).apply()
    }
    protected fun putLong(key: String, value: Long){
        mSP!!.edit().putLong(key, value).apply()
    }

    companion object {
        private val spName = "ruoye.note"
    }
}
