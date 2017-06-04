package com.wuruoye.note.util

import android.content.Context
import com.droi.sdk.DroiCallback
import com.droi.sdk.DroiError
import com.droi.sdk.core.DroiCondition
import com.droi.sdk.core.DroiObject
import com.droi.sdk.core.DroiQuery
import com.droi.sdk.core.DroiUser
import com.wuruoye.note.model.Note
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.model.UpNote
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers

/**
 * Created by wuruoye on 2017/6/4.
 * this file is to do
 */

object BackupUtil{

    fun backupNote(context: Context, listener: BackupUtil.OnBackupListener){
        val noteCache = NoteCache(context)
        var name = ""
        Observable.create(object : ObservableOnSubscribe<DroiUser>{
            override fun subscribe(p0: ObservableEmitter<DroiUser>?) {
                val user = DroiUser.getCurrentUser()
                if (user != null && user.isLoggedIn && user.isAuthorized && !user.isAnonymous){
                    p0!!.onNext(user)
                }else if (noteCache.isLogin){
                    val error = DroiError()
                    DroiUser.login(noteCache.userName,noteCache.userPass,error)
                    if (error.isOk){
                        p0!!.onNext(DroiUser.getCurrentUser())
                    }else{
                        listener.onBackupFail("登录失败")
                    }
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .map(object : Function<DroiUser,ArrayList<UpNote>>{
                    override fun apply(p0: DroiUser?): ArrayList<UpNote> {
                        name = p0!!.userId
                        val cond = DroiCondition.cond("user",DroiCondition.Type.EQ,name)
                        val query = DroiQuery.Builder.newBuilder()
                                .query(UpNote::class.java)
                                .where(cond)
                                .build()
                        val error = DroiError()
                        val list = query.runQuery<UpNote>(error)
                        val l = ArrayList<UpNote>()
                        if (error.isOk){
                            l += list
                        }else{
                        }
                        return l
                    }
                })
                .map(object : Function<ArrayList<UpNote>, ArrayList<UpNote>>{
                    override fun apply(p0: ArrayList<UpNote>?): ArrayList<UpNote> {
                        val list = SQLiteUtil.getAllNote(context)
                        val deleteList = ArrayList<UpNote>()
                        val l = ArrayList<UpNote>()
                        for (i in list){
                            val note = UpNote()
                            note.user = name
                            note.color = i.style
                            note.content = i.content
                            note.year = i.year
                            note.month = i.month
                            note.day = i.day
                            l.add(note)
                        }
                        for (i in p0!!){
                            val year = i.year
                            val month = i.month
                            val day = i.day
                            for (j in l){
                                if (j.month == month && j.year == year && j.day == day){
                                    deleteList.add(i)
                                    break
                                }
                            }
                        }
                        DroiObject.deleteAll(deleteList)
                        return l
                    }
                })
                .map(object : Function<ArrayList<UpNote>, DroiError>{
                    override fun apply(p0: ArrayList<UpNote>?): DroiError {
                        return DroiObject.saveAll(p0)
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { error ->
                    if (error.isOk){
                        listener.onBackupSuccess()
                    }else{
                        listener.onBackupFail(error.toString())
                    }
                }

    }

    fun downloadNote(context: Context, listener: OnBackupListener){
        val noteCache = NoteCache(context)
        var name = ""
        Observable.create(object : ObservableOnSubscribe<DroiUser>{
            override fun subscribe(p0: ObservableEmitter<DroiUser>?) {
                val user = DroiUser.getCurrentUser()
                if (user != null && user.isLoggedIn && user.isAuthorized && !user.isAnonymous){
                    p0!!.onNext(user)
                }else if (noteCache.isLogin){
                    val error = DroiError()
                    DroiUser.login(noteCache.userName,noteCache.userPass,error)
                    if (error.isOk){
                        p0!!.onNext(DroiUser.getCurrentUser())
                    }else{
                        listener.onBackupFail("登录失败")
                    }
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .map(object : Function<DroiUser,ArrayList<UpNote>>{
                    override fun apply(p0: DroiUser?): ArrayList<UpNote> {
                        name = p0!!.userId
                        val cond = DroiCondition.cond("user",DroiCondition.Type.EQ,name)
                        val query = DroiQuery.Builder.newBuilder()
                                .query(UpNote::class.java)
                                .where(cond)
                                .build()
                        val error = DroiError()
                        val list = query.runQuery<UpNote>(error)
                        val l = ArrayList<UpNote>()
                        if (error.isOk){
                            l += list
                        }else{
                        }
                        return l
                    }
                })
                .map(object : Function<ArrayList<UpNote>, String>{
                    override fun apply(p0: ArrayList<UpNote>?): String {
                        try {
                            val list = SQLiteUtil.getAllNote(context)
                            for (i in p0!!){
                                val year = i.year
                                val month = i.month
                                val day = i.day
                                for (j in list){
                                    if (j.year == year && j.month == month && j.day == day){
                                        break;
                                    }
                                }
                                val week = NoteUtil.getWeek(year,month,day)
                                val note = Note(year, month, day, week)
                                note.style = i.color
                                note.content = i.content
                                SQLiteUtil.saveNote(context,note)
                            }
                            return "同步成功"
                        } catch(e: Exception) {
                            return "同步出错"
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Consumer<String>{
                    override fun accept(p0: String?) {
                        listener.onBackupFail(p0!!)
                    }
                })

    }

    fun upNote(context: Context, note: Note){
        val noteCache = NoteCache(context)
        var name = ""
        Observable.create(object : ObservableOnSubscribe<DroiUser>{
            override fun subscribe(p0: ObservableEmitter<DroiUser>?) {
                val user = DroiUser.getCurrentUser()
                if (user != null && user.isLoggedIn && user.isAuthorized && !user.isAnonymous){
                    p0!!.onNext(user)
                }else if (noteCache.isLogin){
                    val error = DroiError()
                    DroiUser.login(noteCache.userName,noteCache.userPass,error)
                    if (error.isOk){
                        p0!!.onNext(DroiUser.getCurrentUser())
                    }else{
                    }
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .map(object : Function<DroiUser, DroiError>{
                    override fun apply(p0: DroiUser?): DroiError {
                        name = p0!!.userId
                        val cond1 = DroiCondition.cond("user",DroiCondition.Type.EQ,name)
                        val cond2 = DroiCondition.cond("year",DroiCondition.Type.EQ,note.year)
                        val cond3 = DroiCondition.cond("month",DroiCondition.Type.EQ,note.month)
                        val cond4 = DroiCondition.cond("day",DroiCondition.Type.EQ,note.day)
                        val query = DroiQuery.Builder.newBuilder()
                                .query(UpNote::class.java)
                                .where(cond1)
                                .build()
                        var error = DroiError()
                        val list = query.runQuery<UpNote>(error)
                        if (error.isOk){
                            for (i in list){
                                if (i.year == note.year && i.month == note.month && i.day == note.day){
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
                        error = upNote.save()
                        return error
                    }
                })
                .subscribe()

    }

    interface OnBackupListener{
        fun onBackupSuccess()
        fun onBackupFail(message: String)
    }
}
