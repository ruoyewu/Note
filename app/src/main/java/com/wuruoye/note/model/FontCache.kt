package com.wuruoye.note.model

import android.content.Context

import com.wuruoye.note.base.BaseCache
import org.json.JSONArray

/**
 * Created by wuruoye on 2017/5/30.
 * this file is to do
 */

class FontCache(context: Context) : BaseCache(context){

    var font: Int
        get() = mSP!!.getInt(FONT, IS_FONT_CHANGE_DEFAULT)
        set(value) = mSP!!.edit().putInt(FONT,value).apply()

    fun getFontDownloadList(): ArrayList<Int>{
        return FONT_DOWNLOAD_LIST_DEFAULT
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
        val FONT = "font"
        val FONT_DOWNLOAD_LIST = "font_download_list"
        val FONT_LIST = "font_list"
        val FONT_LIST_NAME = "font_list_name"

        val IS_FONT_CHANGE_DEFAULT = 0
        val FONT_DOWNLOAD_LIST_DEFAULT = arrayListOf(
                1,2,3,4,5,6
        )
    }
}
