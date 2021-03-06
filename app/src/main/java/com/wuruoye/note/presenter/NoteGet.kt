package com.wuruoye.note.presenter

import android.content.Context
import com.wuruoye.note.base.IAbsPresenter
import com.wuruoye.note.base.IAbsView
import com.wuruoye.note.base.IBasePresenter
import com.wuruoye.note.base.IListener
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Note
import com.wuruoye.note.util.NoteUtil
import com.wuruoye.note.util.SQLiteUtil
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */
class NoteGet(context: Context) : IAbsPresenter<IAbsView<ArrayList<Note>>>(context),
        IListener<ArrayList<Note>>, IBasePresenter {
    enum class Type{
        EXPEND,
        CLOSE
    }

    fun requestNote(month: Int,year: Int,isClose: Boolean){
        if (isClose){
            requestNote(month, year,Type.CLOSE)
        }else{
            requestNote(month, year,Type.EXPEND)
        }
    }

    fun requestNote(search: String){
        val noteList = SQLiteUtil.getAllNote(mContext)
        val list = ArrayList<Note>()
        if (search.trim() == ""){
            onSuccess(list)
        }else{
            for (n in noteList){
                val containDay = Config.numList[n.day].contains(search) || n.day.toString().contains(search)
                val containWeek = Config.weekList[n.week].contains(search) || n.week.toString().contains(search)
                val containContent = n.content.contains(search)
                if (containDay || containWeek || containContent){
                    list.add(n)
                }
            }
            onSuccess(list)
        }
    }

    fun requestNote(year: Int, month: Int, day: Int){
        val note = SQLiteUtil.isContain(mContext, year, month, day)
        if (note != null){
            onSuccess(arrayListOf(note))
        }else {
            val week = NoteUtil.getWeek(year, month, day)
            val n = Note(year, month, day, week)
            onSuccess(arrayListOf(n))
        }
    }

    fun requestAllNote(){
        onSuccess(SQLiteUtil.getAllNote(mContext))
    }

    private fun requestNote(month: Int, year: Int, type: Type){
        val noteList = SQLiteUtil.getNote(mContext, year, month)

        if (type == Type.CLOSE){
            onSuccess(noteList)
        }else {
            val today =
            if (month == NoteUtil.getMonth() && year == NoteUtil.getYear()){
                NoteUtil.getDay()
            }else{
                NoteUtil.getMaxDay(month, year)
            }
            val list = ArrayList<Note>()
            val startNote = NoteUtil.getStartNote(year, month)
            var day = startNote.day
            var week = startNote.week

            list.add(Note(year,month,0,-1))
            for (i in 0..today - 1){
                list.add(Note(year,month,day,week))
                day ++
                week = week % 7 + 1
            }
            list.add(Note(year,month,0,-2))

            for (i in noteList){
                list[i.day] = i
            }
            onSuccess(list)
        }
    }

    override fun onSuccess(model: ArrayList<Note>) {
        getView()?.setModel(model)
    }

    override fun onFail(fail: String) {
        getView()?.setWorn(fail)
    }
}