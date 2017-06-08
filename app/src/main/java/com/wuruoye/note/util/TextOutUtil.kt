package com.wuruoye.note.util

import android.content.Context
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Note
import java.io.*
import java.lang.StringBuilder
import java.util.*

/**
 * Created by wuruoye on 2017/6/7.
 * this file is to do
 */
object TextOutUtil{

    fun outToText(context: Context, listener: TextOutListener){
        val list = SQLiteUtil.getAllNote(context)
        toText(list, listener)
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

    fun getNoteString(context: Context): String{
        val list = SQLiteUtil.getAllNote(context)
        return noteToString(list)
    }

    private fun toText(noteList: ArrayList<Note>, listener: TextOutListener){
        //判断文件夹是否存在
        val direct = File(Config.outDirect)
        if (!direct.exists()){
            direct.mkdirs()
        }

        val outString = noteToString(noteList)
        val outFile = Config.outDirect + "outNote_" + getDate() + ".txt"
        val write = OutputStreamWriter(FileOutputStream(outFile),"gbk")
        val out = BufferedWriter(write)
        try {
            out.write(outString)
            listener.onOutSuccess(outFile)
        } catch(e: Exception) {
            listener.onOutFail("导出文件错误")
        } finally {
            out.close()
            write.close()
        }
    }

    private fun noteToString(noteList: ArrayList<Note>): String{
        Collections.sort(noteList, Comparator<Note> { o1, o2 ->
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

        val build = StringBuilder()
        for (i in noteList){
            build.append(i.year).append("年").append(i.month).append("月").append(i.day).append("日").append("\n")
                    .append(i.content).append("\n\n")
        }
        return build.toString()
    }

    private fun getDate(): String{
        val calender = Calendar.getInstance()
        calender.timeInMillis = System.currentTimeMillis()
        val month = calender.get(Calendar.MONTH) + 1
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)
        val second = calender.get(Calendar.SECOND)

        return StringBuilder().append(month).append("-").append(day)
                .append(" ").append(hour).append(":").append(minute).append(":").append(second)
                .toString()
    }

    interface TextOutListener{
        fun onOutSuccess(path: String)
        fun onOutFail(message: String)
    }
}