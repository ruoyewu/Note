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

    var isAutoBackup: Boolean
        get() = mSP!!.getBoolean(BACKUP, BACKUP_DEFAULT)
        set(value) = mSP!!.edit().putBoolean(BACKUP,value).apply()

    var lastBackup: Long
        get() = mSP!!.getLong(LAST_BACKUP, LAST_BACKUP_DEFAULT)
        set(value) = mSP!!.edit().putLong(LAST_BACKUP,value).apply()

    var isLogin: Boolean
        get() = mSP!!.getBoolean(IS_LOGIN, IS_LOGIN_DEFAULT)
        set(value) = mSP!!.edit().putBoolean(IS_LOGIN, value).apply()

    var userName: String
        get() = mSP!!.getString(USER_NAME, USER_NAME_DEFAULT)
        set(value) = mSP!!.edit().putString(USER_NAME, value).apply()

    var userPass: String
        get() = mSP!!.getString(USER_PASS, USER_PASS_DEFAULT)
        set(value) = mSP!!.edit().putString(USER_PASS, value).apply()

    var isAutoSave: Boolean
        get() = mSP!!.getBoolean(AUTO_SAVE, AUTO_SAVE_DEFAULT)
        set(value) = mSP!!.edit().putBoolean(AUTO_SAVE, value).apply()

    companion object{
        val ITEM_SHOW = "item_show"
        val BACKUP = "isAutoBackup"
        val AUTO_SAVE = "auto_save"
        val LAST_BACKUP = "last_back"
        val IS_LOGIN = "is_login"
        val USER_NAME = "user_name"
        val USER_PASS = "user_pass"

        val ITEM_SHOW_DEFAULT = 1
        val BACKUP_DEFAULT = false
        val AUTO_SAVE_DEFAULT = false
        val LAST_BACKUP_DEFAULT = 0L
        val IS_LOGIN_DEFAULT = false
        val USER_NAME_DEFAULT = ""
        val USER_PASS_DEFAULT = ""
    }
}