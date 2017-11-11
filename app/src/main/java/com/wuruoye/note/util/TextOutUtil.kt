package com.wuruoye.note.util

import android.content.Context
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Date
import com.wuruoye.note.model.Note
import com.wuruoye.note.util.NoteUtil.getDate
import java.io.*
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by wuruoye on 2017/6/7.
 * this file is to do
 */
object TextOutUtil{

    fun outToText(text: String, listener: TextOutListener){
        toText(text, listener)
    }

    fun getNoteString(context: Context, from: Date, to: Date): String{
        val list = SQLiteUtil.getNote(context, from, to)
        return noteToString(list)
    }

    fun getNoteStringList(context: Context, from: Date, to: Date): ArrayList<String>{
        val list = SQLiteUtil.getNote(context, from, to)
        val array = ArrayList<String>()
        list.mapTo(array) { it.year.toString() + "年" + it.month + "月" + it.day + "日" + "\n" + it.content }
        return array
    }

    private fun toText(text: String, listener: TextOutListener){
        //判断文件夹是否存在
        val direct = File(Config.outDirect)
        if (!direct.exists()){
            direct.mkdirs()
        }
        val outFile = Config.outDirect + "outNote_" + getDate() + ".txt"
        FileUtil.writeTextUTF8(outFile, text)
        listener.onOutSuccess(outFile)
    }

    private fun noteToString(noteList: ArrayList<Note>): String{
        val build = StringBuilder()
        for (i in noteList){
            build.append(i.year).append("年").append(i.month).append("月").append(i.day).append("日").append("\n")
                    .append(i.content).append("\n\n")
        }
        return build.toString()
    }

    interface TextOutListener{
        fun onOutSuccess(path: String)
        fun onOutFail(message: String)
    }
}