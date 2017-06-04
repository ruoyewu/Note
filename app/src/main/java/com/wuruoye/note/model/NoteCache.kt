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

    var backup: Boolean
        get() = mSP!!.getBoolean(BACKUP, BACKUP_DEFAULT)
        set(value) = mSP!!.edit().putBoolean(BACKUP,value).apply()

    var lastBackup: Long
        get() = mSP!!.getLong(LAST_BACKUP, LAST_BACKUP_DEFAULT)
        set(value) = mSP!!.edit().putLong(LAST_BACKUP,value).apply()

    companion object{
        val ITEM_SHOW = "item_show"
        val BACKUP = "backup"
        val LAST_BACKUP = "last_back"

        val ITEM_SHOW_DEFAULT = 1
        val BACKUP_DEFAULT = false
        val LAST_BACKUP_DEFAULT = 0L
    }
}