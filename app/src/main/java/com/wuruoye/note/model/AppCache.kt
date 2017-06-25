package com.wuruoye.note.model

import android.content.Context

import com.wuruoye.note.base.BaseCache

/**
 * Created by wuruoye on 2017/6/25.
 * this file is to do
 */

class AppCache(context: Context) : BaseCache(context){

    var appLog: String
        get() = getString(APP_LOG, APP_LOG_DEFAULT)
        set(value) = putString(APP_LOG, value)

    var remoteVersionCode: Int
        get() = getInt(REMOTE_VERSION_CODE, REMOTE_VERSION_CODE_DEFAULT)
        set(value) = putInt(REMOTE_VERSION_CODE, value)

    var remoteVersion: String
        get() = getString(REMOTE_VERSION, REMOTE_VERSION_DEFAULT)
        set(value) = putString(REMOTE_VERSION, value)

    var isSeen: Boolean
        get() = getBoolean(IS_SEEN, IS_SEEN_DEFAULT)
        set(value) = putBoolean(IS_SEEN, value)

    var lastRequest: Long
        get() = getLong(LAST_REQUEST, LAST_REQUEST_DEFAULT)
        set(value) = putLong(LAST_REQUEST, value)

    companion object{
        val APP_LOG = "app_log"
        val REMOTE_VERSION_CODE = "remote_version_code"
        val REMOTE_VERSION = "remote_version"
        val IS_SEEN = "is_seen"
        val LAST_REQUEST = "last_request"

        val APP_LOG_DEFAULT = ""
        val REMOTE_VERSION_CODE_DEFAULT = 0
        val REMOTE_VERSION_DEFAULT = "0.0.0"
        val IS_SEEN_DEFAULT = false
        val LAST_REQUEST_DEFAULT = 0L
    }
}
