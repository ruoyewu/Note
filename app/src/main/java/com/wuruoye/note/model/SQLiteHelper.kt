package com.wuruoye.note.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */
class SQLiteHelper(context: Context,
                   name: String,
                   factory: SQLiteDatabase.CursorFactory?,
                   version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(CREATE_NOTE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    companion object{
        val CREATE_NOTE = "create table note (" +
                "id integer primary key autoincrement," +
                "style integer, " +
                "direct integer," +
                "content text," +
                "year integer," +
                "month integer," +
                "day integer," +
                "week integer)"
    }
}