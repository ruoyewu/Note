package com.wuruoye.note.util

import android.annotation.SuppressLint
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Note
import java.lang.StringBuilder
import java.util.*

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */
@SuppressLint("WrongConstant")
object NoteUtil {
    fun isToday(year: Int, month: Int, day: Int): Boolean{
        val calender = getCalender()
        return calender.get(Calendar.YEAR) == year &&
                calender.get(Calendar.MONTH) + 1 == month &&
                calender.get(Calendar.DAY_OF_MONTH) == day
    }

    fun getDay(): Int{
        return getCalender().get(Calendar.DAY_OF_MONTH)
    }

    fun getMaxDay(month: Int, year: Int): Int{
        val calender = getCalender()
        calender.set(Calendar.YEAR,year)
        calender.set(Calendar.MONTH,month - 1)
        calender.set(Calendar.DAY_OF_MONTH,1)
        return calender.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getMonth(): Int{
        return getCalender().get(Calendar.MONTH) + 1
    }

    fun getYear(): Int{
        return getCalender().get(Calendar.YEAR)
    }

    fun getWeek(): Int{
        return getCalender().get(Calendar.DAY_OF_WEEK)
    }

    fun getWeek(year: Int, month: Int, day: Int): Int{
        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, month - 1)
        calender.set(Calendar.DAY_OF_MONTH, day)
        return calender.get(Calendar.DAY_OF_WEEK)
    }

    fun getTime(): String{
        val calender =  getCalender()
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)
        return Config.numList[hour] + "点" + Config.numList[minute] + "分 : "
    }

    fun getStartNote(year: Int, month: Int): Note{
        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR,year)
        calender.set(Calendar.MONTH,month - 1)
        calender.set(Calendar.DAY_OF_MONTH,1)
        val week = calender.get(Calendar.DAY_OF_WEEK)

        return Note(year,month,1,week)
    }

    fun getTime(year: Int, month: Int, day: Int): Long{
        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, month - 1)
        calender.set(Calendar.DAY_OF_MONTH, day)
        calender.set(Calendar.HOUR_OF_DAY, 0)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)
        return calender.timeInMillis
    }

    fun getDate(): String{
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

    fun getMonthUp(up: Boolean, year: Int, month: Int): Array<Int>{
        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, month - 1)
        if (up) {
            calender.add(Calendar.MONTH, 1)
        }else {
            calender.add(Calendar.MONTH, -1)
        }
        return arrayOf(
                calender.get(Calendar.YEAR), calender.get(Calendar.MONTH) + 1
        )
    }

    fun getDayUp(up: Boolean, year: Int, month: Int, day: Int): Array<Int>{
        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, month - 1)
        calender.set(Calendar.DAY_OF_MONTH, day)
        if (up){
            calender.add(Calendar.DAY_OF_MONTH, 1)
        }else {
            calender.add(Calendar.DAY_OF_MONTH, -1)
        }
        return arrayOf(
                calender.get(Calendar.YEAR), calender.get(Calendar.MONTH) + 1, calender.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun getCalender(): Calendar{
        val calender = Calendar.getInstance()
        calender.timeInMillis = System.currentTimeMillis()
        return calender
    }
}