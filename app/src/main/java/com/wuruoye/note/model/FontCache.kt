package com.wuruoye.note.model

import android.content.Context

import com.wuruoye.note.base.BaseCache
import org.json.JSONArray

/**
 * Created by wuruoye on 2017/5/30.
 * this file is to do
 */

class FontCache(context: Context) : BaseCache(context){

    var isFontChange: Boolean
        get() = mSP!!.getBoolean(IS_FONT_CHANGE, IS_FONT_CHANGE_DEFAULT)
        set(value) = mSP!!.edit().putBoolean(IS_FONT_CHANGE,value).apply()

    fun setFontDownloadList(list: ArrayList<Int>){
        val jsonA = JSONArray()
        for (i in list){
            jsonA.put(i)
        }
        mSP!!.edit().putString(FONT_DOWNLOAD_LIST,jsonA.toString()).apply()
    }

    fun getFontDownloadList(): ArrayList<Int>{
        val str = mSP!!.getString(FONT_DOWNLOAD_LIST,"-1")
        if (str == "-1"){
            return FONT_DOWNLOAD_LIST_DEFAULT
        }else{
            val list = ArrayList<Int>()
            val jsonA = JSONArray(str)
            val size = jsonA.length()
            for (i in 0..size - 1){
                list.add(jsonA.getInt(i))
            }
            return list
        }
    }

    fun getFontList(): ArrayList<Int>{
        val str = mSP!!.getString(FONT_LIST,"-1")
        if (str == "-1"){
            return ArrayList()
        }else{
            val list = ArrayList<Int>()
            val jA = JSONArray(str)
            val size = jA.length()
            for (i in 0..size - 1){
                list.add(jA.getInt(i))
            }
            return list
        }
    }

    fun setFontList(list: ArrayList<Int>){
        val jA = JSONArray()
        for (i in list){
            jA.put(i)
        }
        mSP!!.edit().putString(FONT_LIST,jA.toString()).apply()
    }

    companion object{
        val IS_FONT_CHANGE = "is_font_change"
        val FONT_DOWNLOAD_LIST = "font_download_list"
        val FONT_LIST = "font_list"

        val IS_FONT_CHANGE_DEFAULT = false
        val FONT_DOWNLOAD_LIST_DEFAULT = arrayListOf(
                1,2,3,4,5,6,7,8
        )
    }
}
