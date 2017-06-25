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
        get() = getInt(FONT, IS_FONT_CHANGE_DEFAULT)
        set(value) = putInt(FONT,value)

    var fontSize: Float
        get() = getFloat(FONT_SIZE, FONT_SIZE_DEFAULT)
        set(value) = putFloat(FONT_SIZE, value)

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
        val FONT_LIST = "font_list"
        val FONT_SIZE = "font_size"


        val IS_FONT_CHANGE_DEFAULT = 0
        val FONT_SIZE_DEFAULT = 15f
        val FONT_DOWNLOAD_LIST_DEFAULT = arrayListOf(
                1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17
        )
    }
}
