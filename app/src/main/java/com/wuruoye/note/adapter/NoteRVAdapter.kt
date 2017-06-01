package com.wuruoye.note.adapter

import android.annotation.SuppressLint
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.transitionseverywhere.TransitionManager
import com.wuruoye.note.R
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Note
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.util.NoteUtil

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */
class NoteRVAdapter(
        private val notes: ArrayList<Note>,
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<NoteRVAdapter.ViewHolder>(), View.OnClickListener{
    private var redColor = 0
    private var defaultColor = 0
    private lateinit var noteCache: NoteCache

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: ViewHolder?, p1: Int) {
        val note = notes[p1]
        p0!!.itemView.tag = note
        p0.itemView.setOnClickListener(this)

        setView(note,p0)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): ViewHolder {
        redColor = ActivityCompat.getColor(p0!!.context,R.color.carnation)
        defaultColor = ActivityCompat.getColor(p0.context,R.color.gray)
        noteCache = NoteCache(p0.context)
        val item = noteCache.itemShow
        val view =
                if (item == 1 || item == 2){
                    LayoutInflater.from(p0.context).inflate(R.layout.item_note_1,p0,false)
                }else{
                    LayoutInflater.from(p0.context).inflate(R.layout.item_note_2,p0,false)
                }
        return ViewHolder(view)
    }

    override fun onClick(v: View?) {
        listener.onItemClick(v!!)
    }

    private fun setView(note: Note, p0: ViewHolder){
        p0.wait.setTextColor(defaultColor)
        if (NoteUtil.isToday(note.year,note.month,note.day)){
            p0.wait.text = "待"
        }else{
            p0.wait.text = "逝"
        }
        if (note.week == -1){
            p0.info.visibility = View.GONE
            p0.wait.visibility = View.VISIBLE
            p0.wait.text = "上个月"
        }else if (note.week == -2){
            p0.info.visibility = View.GONE
            p0.wait.visibility = View.VISIBLE
            if (note.month == NoteUtil.getMonth() && note.year == NoteUtil.getYear()){
                p0.wait.text = "明日再续"
            }else{
                p0.wait.text = "下个月"
            }
        }else{
            if (note.content != "" || note.style != 0) {
                val item = noteCache.itemShow
                var day = ""
                var week = ""
                if (item == 1){
                    day = Config.numList[note.day]
                    week = "周${Config.weekList[note.week]}"
                }else if (item == 2){
                    day = note.day.toString()
                    week = "周${Config.weekList[note.week]}"
                }else if (item == 3){
                    day = Config.numList[note.month] + "月" + Config.numList[note.day] + "日"
                    week = "星期${Config.weekList[note.week]}"
                }else if (item == 4){
                    day = Config.yearList[note.year - 2013] + "年" +
                            Config.numList[note.month] + "月" +
                            Config.numList[note.day] + "日"
                    week = "星期${Config.weekList[note.week]}"
                }else if (item == 5){
                    day = note.month.toString() + "月" + note.day + "日"
                    week = "星期" + Config.weekList[note.week]
                }else if (item == 6){
                    day = note.month.toString() + "月" + note.day + "日"
                    week = ""
                }
                p0.wait.visibility = View.GONE
                p0.info.visibility = View.VISIBLE
                p0.day.text = day
                p0.title.text = note.content
                p0.week.text = week
            } else {
                p0.info.visibility = View.GONE
                p0.wait.visibility = View.VISIBLE
                if (note.week == 1 || note.week == 7) {
                    p0.wait.setTextColor(redColor)
                }
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(v: View)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val day = itemView.findViewById(R.id.item_note_day) as TextView
        val week = itemView.findViewById(R.id.item_note_week) as TextView
        val title = itemView.findViewById(R.id.item_note_title) as TextView
        val wait = itemView.findViewById(R.id.item_note_null) as TextView
        val info = itemView.findViewById(R.id.item_note_info) as LinearLayout
    }
}