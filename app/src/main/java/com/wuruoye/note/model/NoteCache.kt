package com.wuruoye.note.model

import android.content.Context
import com.wuruoye.note.base.BaseCache

/**
 * Created by wuruoye on 2017/5/29.
 * this file is to do
 */
class NoteCache(context: Context) : BaseCache(context) {

    var itemShow: Int
        get() = mSP!!.getInt(ITEM_SHOW, ITEM_SHOW_DEFAULT)
        set(value) = mSP!!.edit().putInt(ITEM_SHOW,value).apply()

    var fontShow: Int
        get() = mSP!!.getInt(FONT_SHOW, FONT_SHOW_DEFAULT)
        set(value) = mSP!!.edit().putInt(FONT_SHOW,value).apply()


    companion object{
        val ITEM_SHOW = "item_show"
        val FONT_SHOW = "font_show"

        val ITEM_SHOW_DEFAULT = 1
        val FONT_SHOW_DEFAULT = 0
    }
}