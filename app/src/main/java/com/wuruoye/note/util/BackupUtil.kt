package com.wuruoye.note.util

import android.content.Context
import com.droi.sdk.DroiError
import com.droi.sdk.core.*
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Note
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.model.UpNote
import java.io.File

/**
 * Created by wuruoye on 2017/6/4.
 * this file is to do
 */

object BackupUtil{
    private fun getUser(noteCache: NoteCache): DroiUser?{
        val user = DroiUser.getCurrentUser()
        if (user != null && user.isLoggedIn && user.isAuthorized && !user.isAnonymous){
            return user
        }else if (noteCache.isLogin){
            val error = DroiError()
            DroiUser.login(noteCache.userName, noteCache.userPass, error)
            if (error.isOk){
                return user
            }
        }
        return null
    }

    fun backupNote(context: Context, listener: OnBackupListener){
        val noteCache = NoteCache(context)
        val name: String
        val user = getUser(noteCache)
        if (user == null){
            listener.onBackupFail("登录失败，请重新登录")
        }else{
            name = user.userId
            val cond = DroiCondition.cond("user", DroiCondition.Type.EQ, name)
            val query = DroiQuery.Builder.newBuilder()
                    .query(UpNote::class.java)
                    .where(cond)
                    .build()
            val errorGet = DroiError()
            val list = query.runQuery<UpNote>(errorGet)
            val cloudList = ArrayList<UpNote>()
            if (errorGet.isOk){
                cloudList += list
            }
            val localList = SQLiteUtil.getAllNote(context)
            val deleteList = ArrayList<UpNote>()
            val saveList = ArrayList<UpNote>()

            for (i in localList){
                val note = UpNote()
                note.user = name
                note.color = i.style
                note.content = i.content
                note.year = i.year
                note.month = i.month
                note.day = i.day
                note.direct = i.direct
                if (i.bkImage != ""){
                    note.bkFile = DroiFile(File(Config.imagePath + i.bkImage))
                }
                saveList.add(note)
            }
            for (i in cloudList){
                val year = i.year
                val month = i.month
                val day = i.day
                for (j in saveList){
                    if (j.month == month && j.year == year && j.day == day){
                        deleteList.add(i)
                        break
                    }
                }
            }
            for (i in deleteList){
                if (i.bkFile != null){
                    i.bkFile.delete()
                }
                i.delete()
            }
            val errorSave = DroiObject.saveAll(saveList)
            if (errorSave.isOk){
                listener.onBackupSuccess()
            }else{
                listener.onBackupFail(errorSave.toString())
            }
        }
    }

    fun downloadNote(context: Context, listener: OnBackupListener){
        val noteCache = NoteCache(context)
        val name: String
        val user = getUser(noteCache)
        if (user == null){
            listener.onBackupFail("登录失败")
        }else{
            try {
                name = user.userId
                val cond = DroiCondition.cond("user", DroiCondition.Type.EQ, name)
                val query = DroiQuery.Builder.newBuilder()
                        .query(UpNote::class.java)
                        .where(cond)
                        .build()
                val cloudList = ArrayList<UpNote>()
                val errorGet = DroiError()
                val list = query.runQuery<UpNote>(errorGet)
                if (errorGet.isOk){
                    for (i in list){
                        if (i.bkFile != null){
                            val errorImage = DroiError()
                            val byteArray = i.bkFile.get(errorImage)
                            if (errorImage.isOk){
                                ImageCompressUtil.writeToFile(byteArray, "note_${i.year}-${i.month}-${i.day}")
                            }
                        }
                        cloudList.add(i)
                    }
                }
                val localList = SQLiteUtil.getAllNote(context)
                for (i in cloudList){
                    val year = i.year
                    val month = i.month
                    val day = i.day
                    var exist = false
                    for (j in localList){
                        if (j.year == year && j.month == month && j.day == day){
                            exist = true
                            break
                        }
                    }
                    if (!exist){
                        val week = NoteUtil.getWeek(year,month,day)
                        val note = Note(year, month, day, week)
                        note.style = i.color
                        note.content = i.content
                        if (i.direct != 0){
                            note.direct = i.direct
                        }
                        if (i.bkFile != null){
                            note.bkImage = "note_${i.year}-${i.month}-${i.day}"
                        }
                        SQLiteUtil.saveNote(context,note)
                    }
                }
                listener.onBackupSuccess()
            } catch(e: Exception) {
                listener.onBackupFail("同步出错")
            }
        }
    }

    //保存本地日记的同时保存到云端
    fun upNote(context: Context, note: Note){
        val noteCache = NoteCache(context)
        val name: String
        val user = getUser(noteCache)
        if (user == null){

        }else{
            name = user.userId
            val cond1 = DroiCondition.cond("user",DroiCondition.Type.EQ,name)
            val query = DroiQuery.Builder.newBuilder()
                    .query(UpNote::class.java)
                    .where(cond1)
                    .build()
            var error = DroiError()
            val list = query.runQuery<UpNote>(error)
            if (error.isOk){
                for (i in list){
                    if (i.year == note.year && i.month == note.month && i.day == note.day){
                        if (i.bkFile != null){
                            i.bkFile.delete()
                        }
                        i.delete()
                    }
                }
            }else{
            }
            val upNote = UpNote()
            upNote.user = name
            upNote.content = note.content
            upNote.color = note.style
            upNote.year = note.year
            upNote.month = note.month
            upNote.day = note.day
            upNote.direct = note.direct
            if (note.bkImage != ""){
                upNote.bkFile = DroiFile(File(Config.imagePath + note.bkImage))
            }
            upNote.save()
        }
    }

    interface OnBackupListener{
        fun onBackupSuccess()
        fun onBackupFail(message: String)
    }
}
