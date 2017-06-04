package com.wuruoye.note.util

import android.content.Context
import com.droi.sdk.DroiCallback
import com.droi.sdk.DroiError
import com.droi.sdk.core.DroiCondition
import com.droi.sdk.core.DroiObject
import com.droi.sdk.core.DroiQuery
import com.droi.sdk.core.DroiUser
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
                .observeOn(Schedulers.newThread())
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

    interface OnBackupListener{
        fun onBackupSuccess()
        fun onBackupFail(message: String)
    }
}
