package com.wuruoye.note.model

import android.content.Context
import com.wuruoye.note.base.BaseCache

/**
 * Created by wuruoye on 2017/5/29.
 * this file is to do
 */
class NoteCache(context: Context) : BaseCache(context) {

    var itemShow: Int
        get() = getInt(ITEM_SHOW, ITEM_SHOW_DEFAULT)
        set(value) = putInt(ITEM_SHOW,value)

    var isAutoBackup: Boolean
        get() = getBoolean(BACKUP, BACKUP_DEFAULT)
        set(value) = putBoolean(BACKUP,value)

    var lastBackup: Long
        get() = getLong(LAST_BACKUP, LAST_BACKUP_DEFAULT)
        set(value) = putLong(LAST_BACKUP,value)

    var isLogin: Boolean
        get() = getBoolean(IS_LOGIN, IS_LOGIN_DEFAULT)
        set(value) = putBoolean(IS_LOGIN, value)

    var userName: String
        get() = getString(USER_NAME, USER_NAME_DEFAULT)
        set(value) = putString(USER_NAME, value)

    var userPass: String
        get() = getString(USER_PASS, USER_PASS_DEFAULT)
        set(value) = putString(USER_PASS, value)

    var isAutoSave: Boolean
        get() = getBoolean(AUTO_SAVE, AUTO_SAVE_DEFAULT)
        set(value) = putBoolean(AUTO_SAVE, value)

    var autoState: Int
        get() = getInt(AUTO_STATE, AUTO_STATE_DEFAULT)
        set(value) = putInt(AUTO_STATE, value)

    var isLock: Boolean
        get() = getBoolean(IS_LOCK, IS_LOCK_DEFAULT)
        set(value) = putBoolean(IS_LOCK, value)

    var isFinger: Boolean
        get() = getBoolean(IS_FINGER, IS_FINGER_DEFAULT)
        set(value) = putBoolean(IS_FINGER, value)

    var lockPassword: String
        get() = getString(LOCK_PASSWORD, LOCK_PASSWORD_DEFAULT)
        set(value) = putString(LOCK_PASSWORD, value)

    companion object{
        val ITEM_SHOW = "item_show"
        val BACKUP = "isAutoBackup"
        val AUTO_SAVE = "auto_save"
        val LAST_BACKUP = "last_back"
        val IS_LOGIN = "is_login"
        val USER_NAME = "user_name"
        val USER_PASS = "user_pass"
        val AUTO_STATE = "auto_state"
        val IS_LOCK = "is_lock"
        val IS_FINGER = "is_finger"
        val LOCK_PASSWORD = "lock_password"

        val ITEM_SHOW_DEFAULT = 1
        val BACKUP_DEFAULT = false
        val AUTO_SAVE_DEFAULT = false
        val AUTO_STATE_DEFAULT = 0
        val LAST_BACKUP_DEFAULT = 0L
        val IS_LOGIN_DEFAULT = false
        val USER_NAME_DEFAULT = ""
        val USER_PASS_DEFAULT = ""
        val IS_LOCK_DEFAULT = false
        val IS_FINGER_DEFAULT = false
        val LOCK_PASSWORD_DEFAULT = "0358"
    }
}