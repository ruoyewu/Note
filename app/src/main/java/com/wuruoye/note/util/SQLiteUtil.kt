package com.wuruoye.note.util

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.wuruoye.note.model.Date
import com.wuruoye.note.model.Note
import com.wuruoye.note.model.SQLiteHelper
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */

object SQLiteUtil{
    const val VERSION = 2
    const val NAME = "wuruoye.note"
    const val NOTE_TABLE = "note"

    private var dbHelper: SQLiteHelper? = null

    private fun initHelper(context: Context){
        if (dbHelper == null){
            synchronized(this,{
                if (dbHelper == null){
                    dbHelper = SQLiteHelper(context, NAME,null, VERSION)
                }
            })
        }
    }

    fun saveNote(context: Context, note: Note){
        val n = isContain(context, note.year, note.month, note.day)
        if (n != null){
            note.id = n.id
            updateNote(context,note)
        }else{
            addNote(context,note)
        }
    }

    private fun addNote(context: Context,note: Note){
        initHelper(context)
        val db = dbHelper!!.writableDatabase
        db.insert(NOTE_TABLE,null, getContentValues(note))
    }

    private fun updateNote(context: Context, note: Note){
        initHelper(context)
        val db = dbHelper!!.writableDatabase
        db.update(NOTE_TABLE, getContentValues(note),"id = ?", arrayOf(note.id.toString()))
    }

    fun deleteNote(context: Context,note: Note){
        initHelper(context)
        val db = dbHelper!!.writableDatabase
        db.delete(NOTE_TABLE,"id = ?", arrayOf(note.id.toString()))
    }

    fun getNote(context: Context, year: Int, month: Int): ArrayList<Note>{
        initHelper(context)
        val noteList = ArrayList<Note>()
        val db = dbHelper!!.readableDatabase
        val cursor = db.query(NOTE_TABLE,null,"year = ? and month = ?", arrayOf(year.toString(),month.toString()),null,null,null)
        if (cursor.moveToFirst()){
            do {
                noteList.add(getNote(cursor))
            }while (cursor.moveToNext())
        }
        cursor.close()
        return sortNoteList(noteList)
    }

    fun getAllNote(context: Context): ArrayList<Note>{
        initHelper(context)
        val list = ArrayList<Note>()
        val db = dbHelper!!.readableDatabase
        val cursor = db.query(NOTE_TABLE,null,null,null,null,null,null)
        if (cursor.moveToFirst()){
            do {
                list.add(getNote(cursor))
            }while (cursor.moveToNext())
        }
        cursor.close()
        return sortNoteList(list)
    }

    fun getNote(context: Context, from: Date, to: Date): ArrayList<Note>{
        val allNote = getAllNote(context)
        val list = ArrayList<Note>()
        val fromT = NoteUtil.getTime(from.year, from.month, from.day)
        val toT = NoteUtil.getTime(to.year, to.month, to.day)
        for (i in allNote){
            val time = NoteUtil.getTime(i.year, i.month, i.day)
            if (time in fromT..toT){
                list.add(i)
            }
        }
        return list
    }

    private fun getContentValues(note: Note): ContentValues{
        val values = ContentValues()
        values.put("content",note.content)
        values.put("style",note.style)
        values.put("direct",note.direct)
        values.put("year",note.year)
        values.put("month",note.month)
        values.put("day",note.day)
        values.put("week",note.week)
        values.put("bkImage",note.bkImage)
        return values
    }

    private fun getNote(cursor: Cursor): Note{
        val id = cursor.getInt(cursor.getColumnIndex("id"))
        val style = cursor.getInt(cursor.getColumnIndex("style"))
        val content = cursor.getString(cursor.getColumnIndex("content"))
        val direct = cursor.getInt(cursor.getColumnIndex("direct"))
        val year = cursor.getInt(cursor.getColumnIndex("year"))
        val month = cursor.getInt(cursor.getColumnIndex("month"))
        val day = cursor.getInt(cursor.getColumnIndex("day"))
        val week = cursor.getInt(cursor.getColumnIndex("week"))
        var image = cursor.getString(cursor.getColumnIndex("bkImage"))
        if (image == null){
            image = ""
        }
        return Note(id,style,direct,content, year, month, day, week, image)
    }

    fun sortNoteList(list: ArrayList<Note>): ArrayList<Note>{
        Collections.sort(list, Comparator<Note> { o1, o2 ->
            val x = o1!!.year - o2!!.year
            val y = o1.month - o2.month
            val z = o1.day - o2.day
            if (x == 0){
                if (y == 0){
                    return@Comparator z
                }
                return@Comparator y
            }
            x
        })
        return list
    }

    fun isContain(context: Context, year: Int, month: Int, day: Int): Note?{
        initHelper(context)
        val db = dbHelper!!.readableDatabase
        val cursor = db.query(NOTE_TABLE,null,"year = ? and month = ? and day = ?",
                arrayOf(year.toString(),month.toString(),day.toString()),null,null,null)
        if (cursor.moveToFirst()){
            val note = getNote(cursor)
            cursor.close()
            return note
        }else{
            return null
        }
    }
}