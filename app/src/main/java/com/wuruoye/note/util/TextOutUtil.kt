package com.wuruoye.note.util

import android.content.Context
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Date
import com.wuruoye.note.model.Note
import com.wuruoye.note.util.NoteUtil.getDate
import java.io.*
import java.lang.StringBuilder
import java.util.*

/**
 * Created by wuruoye on 2017/6/7.
 * this file is to do
 */
object TextOutUtil{

    fun outToText(text: String, listener: TextOutListener){
        toText(text, listener)
    }

    fun readText(path: String): String{
        val read = InputStreamReader(FileInputStream(path), "gbk")
        val inFile = BufferedReader(read)
        try {
            return inFile.readText()
        }finally {
            inFile.close()
            read.close()
        }
    }

    fun getNoteString(context: Context, from: Date, to: Date): String{
        val list = SQLiteUtil.getNote(context, from, to)
        return noteToString(list)
    }

    private fun toText(text: String, listener: TextOutListener){
        //判断文件夹是否存在
        val direct = File(Config.outDirect)
        if (!direct.exists()){
            direct.mkdirs()
        }
        val outFile = Config.outDirect + "outNote_" + getDate() + ".txt"
        val write = OutputStreamWriter(FileOutputStream(outFile),"gbk")
        val out = BufferedWriter(write)
        try {
            out.write(text)
            listener.onOutSuccess(outFile)
        } catch(e: Exception) {
            listener.onOutFail("导出文件错误")
        } finally {
            out.close()
            write.close()
        }
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