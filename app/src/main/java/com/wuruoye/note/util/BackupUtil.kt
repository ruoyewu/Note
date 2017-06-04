package com.wuruoye.note.util

import android.content.Context
import com.droi.sdk.DroiCallback
import com.droi.sdk.DroiError
import com.droi.sdk.core.DroiObject
import com.droi.sdk.core.DroiUser
import com.wuruoye.note.model.UpNote

/**
 * Created by wuruoye on 2017/6/4.
 * this file is to do
 */

object BackupUtil{

    fun backupNote(context: Context, listener: BackupUtil.OnBackupListener){
        Thread({
            val user = DroiUser.getCurrentUser()
            if (user != null && user.isAuthorized && !user.isAnonymous && user.isLoggedIn){
                val list = SQLiteUtil.getAllNote(context)
                val upList = ArrayList<UpNote>()
                for (i in list){
                    val note = UpNote()
                    note.user = user.userId
                    note.content = i.content
                    note.color = i.style
                    note.year = i.year
                    note.month = i.month
                    note.day = i.day
                    upList.add(note)
                }
                DroiObject.saveAllInBackground(upList, object : DroiCallback<Boolean> {
                    override fun result(p0: Boolean?, p1: DroiError?) {
                        if (p1!!.isOk){
                            listener.onBackupSuccess()
                        }else{
                            when (p1.code){
                                else -> listener.onBackupFail("备份错误:" + p1.code)
                            }
                        }
                    }
                })
            }
        }).start()
    }

    interface OnBackupListener{
        fun onBackupSuccess()
        fun onBackupFail(message: String)
    }
}
